/**
 * 
 */
package enginerds.hazelcast.cdc.sql;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.cdc.CdcSinks;
import com.hazelcast.jet.cdc.ChangeRecord;
import com.hazelcast.jet.cdc.DebeziumCdcSources;
import com.hazelcast.jet.cdc.DebeziumCdcSources.Builder;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.StreamSource;
import com.hazelcast.map.IMap;
import io.debezium.connector.sqlserver.SqlServerConnector;

/**
 * @author SGopala
 *
 */
public class OpportunityPipeline {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SqlServerConnector sqlConnector = new SqlServerConnector();
		Builder<ChangeRecord> builder = DebeziumCdcSources.debezium("sf_opportunity", sqlConnector.getClass());

		builder.setProperty("database.hostname", "host.docker.internal");
		builder.setProperty("database.port", "1433");
		builder.setProperty("database.user", "ccdba");
		builder.setProperty("database.password", "SForcedev1");
		builder.setProperty("database.servername", "WLA-GSUBT14");
		builder.setProperty("database.dbname", "CONFLICT_CHECK");
		builder.setProperty("database.whitelist", "CONFLICT_CHECK");
		builder.setProperty("table.whitelist", "dbo.SF_OPPORTUNITY__C");
		builder.setProperty("topic", "CONFLICT_CHECK.dbo.SF_OPPORTUNITY__C");
		builder.setProperty("topic.prefix", "cc-sf-opportunity");
		/*
		 * builder.setProperty("schema.history.internal.kafka.topic", "");
		 * builder.setProperty("schema.history.internal.kafka.bootstrap.servers", "");
		 */

		StreamSource<ChangeRecord> source = builder.build();

		Pipeline pipeline = Pipeline.create();
		pipeline.readFrom(source).withoutTimestamps().peek().writeTo(CdcSinks.map("sf_opportunity",
				r -> r.key().toMap().get("id"), r -> r.value().toObject(Opportunity.class).name));

		JobConfig cfg = new JobConfig().setName("sf-opportunity-monitor");
		HazelcastInstance hz = Hazelcast.bootstrappedInstance();

		// Delete map if exists
		IMap<Integer, Object> sfOpportunityMap = hz.getMap("sf_opportunity");
		if (sfOpportunityMap != null)
			sfOpportunityMap.destroy();

		hz.getJet().newJob(pipeline, cfg);

	}

}
