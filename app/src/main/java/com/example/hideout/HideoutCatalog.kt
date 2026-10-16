package com.example.hideout

import com.example.R

object HideoutCatalog {

    fun getSagaArtPieces(sagaId: Int): List<ArtPiece> {
        return getDefaultCatalog().filter { it.sagaId == sagaId }
    }

    fun getSeasonalArtPieces(): List<ArtPiece> {
        return getDefaultCatalog().filter { it.sagaId == 0 }
    }

    fun getArtPieceById(id: String): ArtPiece? {
        val canonicalId = migrateId(id)
        return getDefaultCatalog().firstOrNull { it.id == canonicalId }
    }

    /**
     * Garante compatibilidade caso saves antigos contenham IDs legados do protótipo.
     */
    fun migrateId(id: String): String {
        return when (id) {
            "art_monalisa_cat" -> "monalisa_art"
            "art_mint_vase" -> "jaro_reliquia"
            "art_starry_night" -> "noiteestrelada_art"
            "art_pearl_earring" -> "amulherdobrincoperola_art"
            "art_victorian_clock" -> "cronorelogio_reliquia"
            "art_fox_bust" -> "opensador_escultura"
            "art_vintage_gramophone" -> "caodebexiga_escultura"
            "art_golden_wrench_trophy" -> "homemvitruviano_art"
            "art_stained_butterfly" -> "girassois_art"
            "art_steampunk_sub" -> "relatividade_art"
            "art_astrolabe_clock" -> "ofilhodohomem_art"
            "art_tesla_orb" -> "ovo_reliquia"
            "art_gear_heart" -> "operarios_art"
            "art_cosmic_tapestry" -> "genesis_art"
            "art_mystic_totem" -> "idolodourado_reliquia"
            "art_golden_scarab" -> "mascaratutacamon_reliquia"
            "art_ancient_urn" -> "tabuleta_reliquia"
            "art_sun_stone" -> "venusdemili_escultura"
            "art_royal_crown" -> "graal_reliquia"
            "art_diamond_skull" -> "obeijo_art"
            "art_golden_chalice" -> "venus_art"
            "art_fleece_banner" -> "rondanorturna_art"
            "art_safe_gear_trophy" -> "persistenciamemorias_art"
            else -> id
        }
    }

    fun getDefaultCatalog(): List<ArtPiece> {
        return listOf(
            // =========================================================================
            // === SAGA I: OFICINA RÚSTICA (FASES 1 A 20) - 5 OBRAS OFICIAIS (3X MAIORES) ===
            // =========================================================================
            ArtPiece(
                id = "monalisa_art",
                title = "Mona Lisa",
                author = "Leonardo da Vinci",
                description = "O enigmático e célebre retrato renascentista de Lisa Gherardini. Uma das maiores obras-primas da humanidade.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 195f,
                heightDp = 240f,
                unlockLevel = 1,
                isUnlocked = false,
                iconEmoji = "🖼️",
                sagaId = 1,
                drawableRes = R.drawable.monalisa_art
            ),
            ArtPiece(
                id = "homemvitruviano_art",
                title = "Homem Vitruviano",
                author = "Leonardo da Vinci",
                description = "Estudo clássico das proporções ideais do corpo humano inscrito no círculo e no quadrado.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.RARE,
                widthDp = 275f,
                heightDp = 220f,
                unlockLevel = 5,
                priceCoins = 180,
                iconEmoji = "📐",
                sagaId = 1,
                drawableRes = R.drawable.homemvitruviano_art
            ),
            ArtPiece(
                id = "girassois_art",
                title = "Os Doze Girassóis",
                author = "Vincent van Gogh",
                description = "Série radiante em amarelo e ocre pintada em Arles, transbordando vitalidade e calor.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.RARE,
                widthDp = 185f,
                heightDp = 240f,
                unlockLevel = 10,
                priceCoins = 220,
                iconEmoji = "🌻",
                sagaId = 1,
                drawableRes = R.drawable.girassois_art
            ),
            ArtPiece(
                id = "opensador_escultura",
                title = "O Pensador",
                author = "Auguste Rodin",
                description = "Monumental escultura de bronze representando a reflexão humana profunda e a contemplação universal.",
                type = ArtType.SCULPTURE,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 530f,
                heightDp = 580f,
                unlockLevel = 15,
                priceCoins = 300,
                iconEmoji = "🗿",
                sagaId = 1,
                drawableRes = R.drawable.opensador_escultura
            ),
            ArtPiece(
                id = "cronorelogio_reliquia",
                title = "Cronorrelógio do Destino",
                author = "Relicário Mecânico Místico",
                description = "Relíquia lendária de engrenagens arcanas. Exibida permanentemente sobre o pedestal com acabamento dourado.",
                type = ArtType.RELIC,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 280f,
                heightDp = 240f,
                unlockLevel = 20,
                priceCoins = 350,
                iconEmoji = "🕰️",
                sagaId = 1,
                drawableRes = R.drawable.cronorelogio_reliquia
            ),

            // =========================================================================
            // === SAGA II: JARDIM SECRETO (FASES 21 A 40) - 5 OBRAS OFICIAIS (3X MAIORES) ===
            // =========================================================================
            ArtPiece(
                id = "noiteestrelada_art",
                title = "A Noite Estrelada",
                author = "Vincent van Gogh",
                description = "Céu ondulante em turbilhões de azul e ouro sobre Saint-Rémy-de-Provence.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 390f,
                heightDp = 480f,
                unlockLevel = 21,
                priceCoins = 260,
                iconEmoji = "🌌",
                sagaId = 2,
                drawableRes = R.drawable.noiteestrelada_art
            ),
            ArtPiece(
                id = "domingoatarde_art",
                title = "Tarde de Domingo na Grande Jatte",
                author = "Georges Seurat",
                description = "Obra-prima do pontilhismo que capta a tranquilidade luminosa de um passeio às margens do Sena.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.RARE,
                widthDp = 330f,
                heightDp = 210f,
                unlockLevel = 25,
                priceCoins = 240,
                iconEmoji = "🌳",
                sagaId = 2,
                drawableRes = R.drawable.domingoatarde_art
            ),
            ArtPiece(
                id = "meninosoltandopipa_art",
                title = "Menino Soltando Pipa",
                author = "Candido Portinari",
                description = "Cena lírica da infância brasileira com cores vibrantes e movimento poético contra o céu límpido.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.COMMON,
                widthDp = 180f,
                heightDp = 240f,
                unlockLevel = 30,
                priceCoins = 200,
                iconEmoji = "🪁",
                sagaId = 2,
                drawableRes = R.drawable.meninosoltandopipa_art
            ),
            ArtPiece(
                id = "caodebexiga_escultura",
                title = "Cão de Bexiga (Balloon Dog)",
                author = "Jeff Koons",
                description = "Ícone contemporâneo de aço inoxidável espelhado e acabamento cromado radiante.",
                type = ArtType.SCULPTURE,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.RARE,
                widthDp = 240f,
                heightDp = 275f,
                unlockLevel = 35,
                priceCoins = 280,
                iconEmoji = "🎈",
                sagaId = 2,
                drawableRes = R.drawable.caodebexiga_escultura
            ),
            ArtPiece(
                id = "jaro_reliquia",
                title = "Jarro Cerimonial de Terracota",
                author = "Ateliê Helenístico Clássico",
                description = "Ânfora sagrada com arabescos de folhas de acanto e vinho, exposta com honra sobre o pedestal.",
                type = ArtType.RELIC,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.COMMON,
                widthDp = 260f,
                heightDp = 220f,
                unlockLevel = 40,
                priceCoins = 310,
                iconEmoji = "🏺",
                sagaId = 2,
                drawableRes = R.drawable.jaro_reliquia
            ),

            // =========================================================================
            // === SAGA III: LABORATÓRIO STEAMPUNK (FASES 41 A 60) - 5 OBRAS OFICIAIS (3X MAIORES) ===
            // =========================================================================
            ArtPiece(
                id = "persistenciamemorias_art",
                title = "A Persistência da Memória",
                author = "Salvador Dalí",
                description = "Os relógios derretidos em paisagem onírica simbolizando a maleabilidade e ilusão do tempo.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 335f,
                heightDp = 180f,
                unlockLevel = 41,
                priceCoins = 320,
                iconEmoji = "⏳",
                sagaId = 3,
                drawableRes = R.drawable.persistenciamemorias_art
            ),
            ArtPiece(
                id = "relatividade_art",
                title = "Relatividade",
                author = "M.C. Escher",
                description = "Litografia com escadarias impossíveis desafiando a gravidade em múltiplas perspectivas geométricas.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.RARE,
                widthDp = 230f,
                heightDp = 230f,
                unlockLevel = 45,
                priceCoins = 260,
                iconEmoji = "🔄",
                sagaId = 3,
                drawableRes = R.drawable.relatividade_art
            ),
            ArtPiece(
                id = "ofilhodohomem_art",
                title = "O Filho do Homem",
                author = "René Magritte",
                description = "O homem de terno e chapéu-coco com uma maçã verde flutuando misteriosamente diante de seu rosto.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.RARE,
                widthDp = 180f,
                heightDp = 240f,
                unlockLevel = 50,
                priceCoins = 270,
                iconEmoji = "🍏",
                sagaId = 3,
                drawableRes = R.drawable.ofilhodohomem_art
            ),
            ArtPiece(
                id = "operarios_art",
                title = "Operários",
                author = "Tarsila do Amaral",
                description = "Pintura emblemática do modernismo retratando a diversidade dos trabalhadores e a industrialização brasileira.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 230f,
                heightDp = 230f,
                unlockLevel = 55,
                priceCoins = 330,
                iconEmoji = "🏭",
                sagaId = 3,
                drawableRes = R.drawable.operarios_art
            ),
            ArtPiece(
                id = "ovo_reliquia",
                title = "Ovo Imperial Fabergé",
                author = "Peter Carl Fabergé",
                description = "Joia mecânica em ouro esmaltado e platina com surpresa em miniatura, preservada no pedestal.",
                type = ArtType.RELIC,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 280f,
                heightDp = 220f,
                unlockLevel = 60,
                priceCoins = 360,
                iconEmoji = "🥚",
                sagaId = 3,
                drawableRes = R.drawable.ovo_reliquia
            ),

            // =========================================================================
            // === SAGA IV: TEMPLO ANCESTRAL (FASES 61 A 80) - 5 OBRAS OFICIAIS (3X MAIORES) ===
            // =========================================================================
            ArtPiece(
                id = "venusdemili_escultura",
                title = "Vênus de Milo",
                author = "Alexandros de Antioquia",
                description = "Famosa estátua de mármore de Afrodite descoberta na ilha de Milos, símbolo supremo da elegância clássica.",
                type = ArtType.SCULPTURE,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 100f,
                heightDp = 330f,
                unlockLevel = 61,
                priceCoins = 360,
                iconEmoji = "🏛️",
                sagaId = 4,
                drawableRes = R.drawable.venusdemili_escultura
            ),
            ArtPiece(
                id = "mascaratutacamon_reliquia",
                title = "Máscara Mortuária de Tutancâmon",
                author = "Ourivesaria Real Tebana",
                description = "Máscara de ouro maciço de 24 quilates com incrustações de lápis-lazúli e cornalina, exposta sobre o pedestal.",
                type = ArtType.RELIC,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 300f,
                heightDp = 200f,
                unlockLevel = 68,
                priceCoins = 390,
                iconEmoji = "👑",
                sagaId = 4,
                drawableRes = R.drawable.mascaratutacamon_reliquia
            ),
            ArtPiece(
                id = "nefeti_reliquia",
                title = "Busto da Rainha Nefertiti",
                author = "Tutmés (Amarna)",
                description = "Escultura de calcário estucado que eterniza a beleza e poder da Grande Esposa Real do Egito.",
                type = ArtType.RELIC,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 160f,
                heightDp = 280f,
                unlockLevel = 72,
                priceCoins = 380,
                iconEmoji = "🧕",
                sagaId = 4,
                drawableRes = R.drawable.nefeti_reliquia
            ),
            ArtPiece(
                id = "tabuleta_reliquia",
                title = "Tabuleta da Criação (Enuma Elish)",
                author = "Escribas da Babilônia Antiga",
                description = "Placa de argila com escrita cuneiforme narrando os mitos primordiais da criação do universo.",
                type = ArtType.RELIC,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.RARE,
                widthDp = 300f,
                heightDp = 210f,
                unlockLevel = 76,
                priceCoins = 340,
                iconEmoji = "📜",
                sagaId = 4,
                drawableRes = R.drawable.tabuleta_reliquia
            ),
            ArtPiece(
                id = "idolodourado_reliquia",
                title = "Ídolo Dourado da Fertilidade",
                author = "Civilização Chachapoya",
                description = "Ídolo maciço forjado em ouro solar com olhos de esmeralda, guardado no topo do pedestal do esconderijo.",
                type = ArtType.RELIC,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 300f,
                heightDp = 210f,
                unlockLevel = 80,
                priceCoins = 410,
                iconEmoji = "🗿",
                sagaId = 4,
                drawableRes = R.drawable.idolodourado_reliquia
            ),

            // =========================================================================
            // === SAGA V: COFRE DO TESOURO (FASES 81 A 100) - 5 OBRAS OFICIAIS (3X MAIORES) ===
            // =========================================================================
            ArtPiece(
                id = "genesis_art",
                title = "A Criação de Adão (Gênesis)",
                author = "Michelangelo Buonarroti",
                description = "Afresco do teto da Capela Sistina com o icônico toque dos dedos entre o Criador e Adão.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 350f,
                heightDp = 240f,
                unlockLevel = 81,
                priceCoins = 420,
                iconEmoji = "☝️",
                sagaId = 5,
                drawableRes = R.drawable.genesis_art
            ),
            ArtPiece(
                id = "obeijo_art",
                title = "O Beijo",
                author = "Gustav Klimt",
                description = "Obra da fase dourada decorada com folhas de ouro, celebrando o amor e a união mística.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 340f,
                heightDp = 180f,
                unlockLevel = 88,
                priceCoins = 400,
                iconEmoji = "💋",
                sagaId = 5,
                drawableRes = R.drawable.obeijo_art
            ),
            ArtPiece(
                id = "venus_art",
                title = "O Nascimento de Vênus",
                author = "Sandro Botticelli",
                description = "A deusa do amor emergindo do mar sobre uma concha gigante impulsionada pelos ventos de Zéfiro.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 305f,
                heightDp = 220f,
                unlockLevel = 92,
                priceCoins = 410,
                iconEmoji = "🐚",
                sagaId = 5,
                drawableRes = R.drawable.venus_art
            ),
            ArtPiece(
                id = "rondanorturna_art",
                title = "A Ronda Noturna",
                author = "Rembrandt van Rijn",
                description = "Monumental pintura barroca holandesa com virtuoso jogo de claro-escuro e energia dramática.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 330f,
                heightDp = 220f,
                unlockLevel = 96,
                priceCoins = 430,
                iconEmoji = "🛡️",
                sagaId = 5,
                drawableRes = R.drawable.rondanorturna_art
            ),
            ArtPiece(
                id = "graal_reliquia",
                title = "O Santo Graal",
                author = "Lenda Arturiana",
                description = "O cálice sagrado das buscas cavaleirescas, irradiando luz celestial sobre o pedestal oficial.",
                type = ArtType.RELIC,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.LEGENDARY,
                widthDp = 230f,
                heightDp = 230f,
                unlockLevel = 100,
                priceCoins = 500,
                iconEmoji = "🏆",
                sagaId = 5,
                drawableRes = R.drawable.graal_reliquia
            ),

            // =========================================================================
            // === 13 OBRAS SAZONAIS & RECOMPENSAS EXTRAS DO ESCONDERIJO (3X MAIORES) ===
            // =========================================================================
            ArtPiece(
                id = "abapura_art",
                title = "Abaporu",
                author = "Tarsila do Amaral",
                description = "Marco inicial do movimento antropofágico brasileiro, com cores tropicais intensas e figura monumental.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 175f,
                heightDp = 240f,
                unlockLevel = 1,
                priceCoins = 450,
                iconEmoji = "🌵",
                sagaId = 6,
                drawableRes = R.drawable.abapura_art,
                seasonalEventName = "Semana de Arte Moderna"
            ),
            ArtPiece(
                id = "ogrito_art",
                title = "O Grito",
                author = "Edvard Munch",
                description = "Expressão angustiante sob um céu avermelhado em Oslo, capturando o drama existencial com traços sinuosos.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 335f,
                heightDp = 180f,
                unlockLevel = 1,
                priceCoins = 420,
                iconEmoji = "😱",
                sagaId = 6,
                drawableRes = R.drawable.ogrito_art,
                seasonalEventName = "Noite das Sombras"
            ),
            ArtPiece(
                id = "amulherdobrincoperola_art",
                title = "Moça com Brinco de Pérola",
                author = "Johannes Vermeer",
                description = "A 'Mona Lisa do Norte' com olhar cativante, turbante azul exótico e o famoso reflexo no brinco de pérola.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 245f,
                heightDp = 220f,
                unlockLevel = 1,
                priceCoins = 390,
                iconEmoji = "💎",
                sagaId = 6,
                drawableRes = R.drawable.amulherdobrincoperola_art,
                seasonalEventName = "Coleção Diamante"
            ),
            ArtPiece(
                id = "onda_art",
                title = "A Grande Onda de Kanagawa",
                author = "Katsushika Hokusai",
                description = "Célebre xilogravura japonesa onde a imensa onda emoldura o Monte Fuji com azul da Prússia inesquecível.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 330f,
                heightDp = 210f,
                unlockLevel = 1,
                priceCoins = 440,
                iconEmoji = "🌊",
                sagaId = 6,
                drawableRes = R.drawable.onda_art,
                seasonalEventName = "Mares do Oriente"
            ),
            ArtPiece(
                id = "americangoth_art",
                title = "American Gothic",
                author = "Grant Wood",
                description = "Retrato severo de um fazendeiro com forquilha e sua filha diante da casa de estilo gótico rural americano.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.RARE,
                widthDp = 240f,
                heightDp = 220f,
                unlockLevel = 1,
                priceCoins = 330,
                iconEmoji = "🌾",
                sagaId = 6,
                drawableRes = R.drawable.americangoth_art,
                seasonalEventName = "Colheita de Outono"
            ),
            ArtPiece(
                id = "falcaonoturno_art",
                title = "Falcões da Noite (Nighthawks)",
                author = "Edward Hopper",
                description = "Cena noturna de um restaurante envidraçado no centro de Nova York com iluminação fluorescente e melancolia retrô.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.RARE,
                widthDp = 335f,
                heightDp = 250f,
                unlockLevel = 1,
                priceCoins = 350,
                iconEmoji = "☕",
                sagaId = 0,
                drawableRes = R.drawable.falcaonoturno_art,
                seasonalEventName = "Noturno Retrô"
            ),
            ArtPiece(
                id = "adanca_art",
                title = "A Dança",
                author = "Henri Matisse",
                description = "Figuras rítmicas de vermelho escarlate rodopiando em união contra campos puros de verde e azul fauvista.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.RARE,
                widthDp = 335f,
                heightDp = 220f,
                unlockLevel = 1,
                priceCoins = 340,
                iconEmoji = "💃",
                sagaId = 0,
                drawableRes = R.drawable.adanca_art,
                seasonalEventName = "Carnaval das Cores"
            ),
            ArtPiece(
                id = "caminhantedasnuvens_art",
                title = "Caminhante sobre o Mar de Névoa",
                author = "Caspar David Friedrich",
                description = "A quintessência do romantismo sublime: um viajante solitário no cume contemplando os abismos cobertos de névoa.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.RARE,
                widthDp = 180f,
                heightDp = 240f,
                unlockLevel = 1,
                priceCoins = 360,
                iconEmoji = "🏔️",
                sagaId = 0,
                drawableRes = R.drawable.caminhantedasnuvens_art,
                seasonalEventName = "Cume das Nuvens"
            ),
            ArtPiece(
                id = "homensjogandocartas_art",
                title = "Os Jogadores de Cartas",
                author = "Paul Cézanne",
                description = "Composição austera e monumental de camponeses concentrados numa partida com paleta de tons terrosos.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.COMMON,
                widthDp = 335f,
                heightDp = 210f,
                unlockLevel = 1,
                priceCoins = 290,
                iconEmoji = "🃏",
                sagaId = 0,
                drawableRes = R.drawable.homensjogandocartas_art,
                seasonalEventName = "Clube dos Mestres"
            ),
            ArtPiece(
                id = "cirandainfantil_art",
                title = "Ciranda Infantil",
                author = "Candido Portinari",
                description = "Celebração calorosa das brincadeiras de roda e da alegria pura das crianças em terras brasileiras.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.RARE,
                widthDp = 305f,
                heightDp = 220f,
                unlockLevel = 1,
                priceCoins = 310,
                iconEmoji = "🎡",
                sagaId = 0,
                drawableRes = R.drawable.cirandainfantil_art,
                seasonalEventName = "Festival da Primavera"
            ),
            ArtPiece(
                id = "capiravaapatica_art",
                title = "Capivara Apática",
                author = "Ateliê do Esconderijo",
                description = "A lendária guardiã serena e imperturbável do esconderijo. Símbolo oficial de tranquilidade e maestria nos quebra-cabeças.",
                type = ArtType.PAINTING,
                zone = PlacementZone.WALL,
                rarity = ArtRarity.LEGENDARY,
                widthDp = 175f,
                heightDp = 240f,
                unlockLevel = 1,
                priceCoins = 400,
                iconEmoji = "🐾",
                sagaId = 0,
                drawableRes = R.drawable.capiravaapatica_art,
                seasonalEventName = "Mascote Oficial do Jogo"
            ),
            ArtPiece(
                id = "profeta_escultura",
                title = "O Profeta",
                author = "Aleijadinho (Antônio Francisco Lisboa)",
                description = "Monumental estátua esculpida em pedra-sabão do barroco de Congonhas com postura dramática e expressividade inigualável.",
                type = ArtType.SCULPTURE,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 330f,
                heightDp = 240f,
                unlockLevel = 1,
                priceCoins = 420,
                iconEmoji = "🗿",
                sagaId = 0,
                drawableRes = R.drawable.profeta_escultura,
                seasonalEventName = "Patrimônio Histórico"
            ),
            ArtPiece(
                id = "elefantedejade_escultura",
                title = "Elefante Imperial de Jade",
                author = "Escultores da Dinastia Ming",
                description = "Esculpido em um único bloco de jade verde translúcido com adereços cerimoniais dourados, atraindo prosperidade ao esconderijo.",
                type = ArtType.SCULPTURE,
                zone = PlacementZone.FLOOR,
                rarity = ArtRarity.MASTERPIECE,
                widthDp = 335f,
                heightDp = 250f,
                unlockLevel = 1,
                priceCoins = 450,
                iconEmoji = "🐘",
                sagaId = 0,
                drawableRes = R.drawable.elefantedejade_escultura,
                seasonalEventName = "Festival da Lua Dourada"
            )
        )
    }
}
