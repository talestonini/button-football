package button.football.domain.model.projection

import button.football.domain.model.Country
import button.football.domain.model.Team
import button.football.domain.model.TeamType
import org.springframework.data.rest.core.config.Projection

/**
 * Created by talestonini on 30/08/2015.
 */
@Projection(name = 'team', types = Team.class)
interface TeamProjection {

    String getName()

    TeamType getTeamType()

    String getFullName()

    String getFoundation()

    String getCity()

    Country getCountry()

    String getLogoImgFile()
}
