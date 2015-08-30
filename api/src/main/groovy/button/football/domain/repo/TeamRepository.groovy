package button.football.domain.repo

import button.football.domain.model.Team
import button.football.domain.model.TeamType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param

/**
 * Created by talestonini on 29/08/2015.
 */
interface TeamRepository extends JpaRepository<Team, Long> {

    Set<TeamType> findByTeamTypeId(@Param(value = 'teamTypeId') Long teamTypeId)

    Set<Team> findByCountryId(@Param(value = 'countryId') Long countryId)
}
