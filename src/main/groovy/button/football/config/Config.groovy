package button.football.config

import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Created by talestonini on 9/03/2016.
 */
@Configuration
public class MainConfig {

    @Bean
    public BasicDataSource dataSource() throws URISyntaxException {

        URI dbUri = new URI(System.getenv("CLEARDB_DATABASE_URL"))

        def username = dbUri.userInfo.split(':')[0]
        def password = dbUri.userInfo.split(':')[1]
        def dbUrl = "jdbc:mysql://${dbUri.host}${dbUri.path}"

        BasicDataSource basicDataSource = new BasicDataSource()
        basicDataSource.setUrl(dbUrl)
        basicDataSource.setUsername(username)
        basicDataSource.setPassword(password)

        basicDataSource
    }
}