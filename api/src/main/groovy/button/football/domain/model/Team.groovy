package button.football.domain.model

import com.fasterxml.jackson.annotation.JsonBackReference
import groovy.transform.EqualsAndHashCode

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * Created by talestonini on 29/08/2015.
 */
@EqualsAndHashCode
@Entity
class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    String name

    @JsonBackReference('teamType-team')
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    TeamType teamType

    String fullName

    String foundation

    String city

    @JsonBackReference('country-team')
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    Country country

    String logoImgFile

    @Override
    String toString() {
        name
    }
}
