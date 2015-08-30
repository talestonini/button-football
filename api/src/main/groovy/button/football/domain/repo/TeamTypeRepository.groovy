package button.football.domain.repo

import button.football.domain.model.TeamType
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by talestonini on 29/08/2015.
 */
interface TeamTypeRepository extends JpaRepository<TeamType, Long> {
}
