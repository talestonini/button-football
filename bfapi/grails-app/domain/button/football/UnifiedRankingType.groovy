package button.football

class UnifiedRankingType {

    String name
    TeamType teamType
    Short listOrder

    static hasMany = [unifiedRankings: UnifiedRanking,
                      championshipTypes: ChampionshipType,
                      countries: Country]

    static belongsTo = [TeamType, ChampionshipType, Country]

    static mapping = {
        championshipTypes joinTable: [name: 'mm_unif_rk_type_champ_type', key: 'unified_ranking_type_id']
        countries joinTable: [name: 'mm_unif_rk_type_country', key: 'unified_ranking_type_id']
        version false
    }

    static constraints = {
        name maxSize: 40, unique: true
        listOrder nullable: true
    }
}
