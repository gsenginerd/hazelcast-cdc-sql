/**
 * 
 */
package enginerds.hazelcast.cdc.sql;

import com.fasterxml.jackson.databind.ext.SqlBlobSerializer;
import com.hazelcast.jet.cdc.ChangeRecord;
import com.hazelcast.jet.cdc.DebeziumCdcSources;
import com.hazelcast.jet.cdc.DebeziumCdcSources.Builder;
import com.hazelcast.jet.pipeline.StreamSource;
import com.hazelcast.sql.SqlService;
import com.hazelcast.sql.impl.schema.SqlCatalog;

import io.debezium.connector.sqlserver.SqlServerConnector;

/**
 * @author SGopala
 *
 */
public class Opportunity {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SqlServerConnector sqlConnector = new SqlServerConnector();
		Builder<ChangeRecord> builder= DebeziumCdcSources.debezium("sf_opportunity", sqlConnector.getClass());
		builder.setProperty("database.hostname", "slv-haldevdb");
		builder.setProperty("database.port", "1433");
		builder.setProperty("database.user", "conflict_check_admin");
		builder.setProperty("database.password", "SForcedev1");
		builder.setProperty("database.dbname", "CONFLICT_CHECK");
		
		StreamSource<ChangeRecord> source = builder.build();
		
	

	}

}
