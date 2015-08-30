package button.football.domain.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import groovy.transform.EqualsAndHashCode

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * Created by talestonini on 29/08/2015.
 */
@EqualsAndHashCode(excludes = ['teams'])
@Entity
class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    String name

    @JsonManagedReference('country-team')
    @OneToMany(mappedBy = 'country', fetch = FetchType.LAZY)
    Set<Team> teams

    @Override
    String toString() {
        name
    }
}
