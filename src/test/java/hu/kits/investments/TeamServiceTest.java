package hu.kits.investments;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;

public class TeamServiceTest {

    
    @BeforeEach
    private void init() throws Exception {
        DataSource dataSource = InMemoryDataSourceFactory.createDataSource();
        
    }
    
}
