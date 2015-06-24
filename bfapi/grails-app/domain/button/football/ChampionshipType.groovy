package button.football

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ChampionshipType {

    String name
    TeamType teamType
    Integer numEditions
    Short weight
    String logoImgFile
    Short listOrder

    // static hasMany = [championships: Championship,
    //                   scores: Score,
    //                   rankings: Ranking,
    //                   unifiedRankingTypes: UnifiedRankingType]

    static belongsTo = [TeamType]

    static mapping = {
        unifiedRankingTypes joinTable: [name: 'mm_unif_rk_type_champ_type', key: 'championship_type_id']
        version false
    }

    static constraints = {
        name maxSize: 40, unique: true
        logoImgFile nullable: true
        listOrder nullable: true
    }

    @Override
    String toString() {
        name
    }
}
