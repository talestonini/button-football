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
class TeamType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    String description

    @JsonManagedReference('teamType-team')
    @OneToMany(mappedBy = 'teamType', fetch = FetchType.LAZY)
    Set<Team> teams

    @Override
    String toString() {
        description
    }
}
