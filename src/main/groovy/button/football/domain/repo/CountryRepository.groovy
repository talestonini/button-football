package button.football.domain.repo

import button.football.domain.model.Country
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by talestonini on 30/08/2015.
 */
interface CountryRepository extends JpaRepository<Country, Long> {
}
