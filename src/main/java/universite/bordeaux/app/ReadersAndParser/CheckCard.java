package universite.bordeaux.app.ReadersAndParser;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author mlfarfan
 * to verify that the name of the card is a valid name
 *
 */
public final class CheckCard {

    private CheckCard(){}
    /**
     * @param card string name card
     * @return the name standard
     */
    public static String verifyCard(String card) {
        String result = card.trim().replaceAll("\\s+", "")
            .replaceAll("'", "").toLowerCase();
        ArrayList<String> cardList = new ArrayList<>(Arrays.asList("cellar",
                                                                   "chapel",
                                                                   "envoy",
                                                                   "moat",
                                                                   "chancellor",
                                                                   "village",
                                                                   "woodcutter",
                                                                   "workshop",
                                                                   "bureaucrat",
                                                                   "feast",
                                                                   "gardens",
                                                                   "militia",
                                                                   "moneylender",
                                                                   "remodel",
                                                                   "smithy",
                                                                   "spy",
                                                                   "thief",
                                                                   "throneroom",
                                                                   "tactician",
                                                                   "councilroom",
                                                                   "festival",
                                                                   "laboratory",
                                                                   "library",
                                                                   "market",
                                                                   "mine",
                                                                   "witch",
                                                                   "adventurer",
                                                                   "courtyard",
                                                                   "pawn",
                                                                   "secretchamber",
                                                                   "greathall",
                                                                   "masquerade",
                                                                   "shantytown",
                                                                   "steward",
                                                                   "swindler",
                                                                   "wishingwell",
                                                                   "baron",
                                                                   "bridge",
                                                                   "conspirator",
                                                                   "coppersmith",
                                                                   "ironworks",
                                                                   "miningvillage",
                                                                   "scout",
                                                                   "duke",
                                                                   "minion",
                                                                   "saboteur",
                                                                   "torturer",
                                                                   "tradingpost",
                                                                   "tribute",
                                                                   "upgrade",
                                                                   "harem",
                                                                   "nobles",
                                                                   "embargo",
                                                                   "haven",
                                                                   "lighthouse",
                                                                   "nativevillage",
                                                                   "pearldiver",
                                                                   "ambassador",
                                                                   "fishingvillage",
                                                                   "lookout",
                                                                   "smugglers",
                                                                   "warehouse",
                                                                   "caravan",
                                                                   "cutpurse",
                                                                   "island",
                                                                   "navigator",
                                                                   "pirateship",
                                                                   "salvager",
                                                                   "seahag",
                                                                   "treasuremap",
                                                                   "bazaar",
                                                                   "explorer",
                                                                   "ghostship",
                                                                   "merchantship",
                                                                   "outpost",
                                                                   "tactitian",
                                                                   "treasury",
                                                                   "wharf",
                                                                   "transmute",
                                                                   "vineyard",
                                                                   "apothecary",
                                                                   "herbalist",
                                                                   "scryingpool",
                                                                   "university",
                                                                   "alchemist",
                                                                   "familiar",
                                                                   "philosophersstone",
                                                                   "golem",
                                                                   "potion",
                                                                   "apprentice",
                                                                   "possession",
                                                                   "loan",
                                                                   "traderoute",
                                                                   "watchtower",
                                                                   "bishop",
                                                                   "monument",
                                                                   "quarry",
                                                                   "talisman",
                                                                   "workersvillage",
                                                                   "city",
                                                                   "contraband",
                                                                   "countinghouse",
                                                                   "mint",
                                                                   "mountebank",
                                                                   "rabble",
                                                                   "royalseal",
                                                                   "vault",
                                                                   "venture",
                                                                   "goons",
                                                                   "grandmarket",
                                                                   "hoard",
                                                                   "bank",
                                                                   "expand",
                                                                   "forge",
                                                                   "kingscourt",
                                                                   "peddler",
                                                                   "platinum",
                                                                   "colony",
                                                                   "bagofgold",
                                                                   "diadem",
                                                                   "followers",
                                                                   "princess",
                                                                   "trustysteed",
                                                                   "hamlet",
                                                                   "fortuneteller",
                                                                   "menagerie",
                                                                   "farmingvillage",
                                                                   "horsetraders",
                                                                   "remake",
                                                                   "tournament",
                                                                   "youngwitch",
                                                                   "harvest",
                                                                   "hornofplenty",
                                                                   "huntingparty",
                                                                   "jester",
                                                                   "fairgrounds",
                                                                   "crossroads",
                                                                   "duchess",
                                                                   "foolsgold",
                                                                   "develop",
                                                                   "oasis",
                                                                   "oracle",
                                                                   "scheme",
                                                                   "tunnel",
                                                                   "jackofalltrades",
                                                                   "noblebrigand",
                                                                   "nomadcamp",
                                                                   "silkroad",
                                                                   "spicemerchant",
                                                                   "trader",
                                                                   "cache",
                                                                   "cartographer",
                                                                   "embassy",
                                                                   "haggler",
                                                                   "highway",
                                                                   "illgottengains",
                                                                   "inn",
                                                                   "mandarin",
                                                                   "margrave",
                                                                   "stable",
                                                                   "bordervillage",
                                                                   "farmland",
                                                                   "abandonedmine",
                                                                   "madman",
                                                                   "mercenary",
                                                                   "ruinedlibrary",
                                                                   "ruinedmarket",
                                                                   "ruinedvillage",
                                                                   "spoils",
                                                                   "survivors",
                                                                   "hovel",
                                                                   "necropolis",
                                                                   "overgrownestate",
                                                                   "poorhouse",
                                                                   "beggar",
                                                                   "squire",
                                                                   "vagrant",
                                                                   "forager",
                                                                   "hermit",
                                                                   "marketsquare",
                                                                   "sage",
                                                                   "storeroom",
                                                                   "urchin",
                                                                   "armory",
                                                                   "deathcart",
                                                                   "feodum",
                                                                   "fortress",
                                                                   "ironmonger",
                                                                   "marauder",
                                                                   "procession",
                                                                   "rats",
                                                                   "scavenger",
                                                                   "sirmartin",
                                                                   "wanderingminstrel",
                                                                   "bandofmisfits",
                                                                   "banditcamp",
                                                                   "catacombs",
                                                                   "count",
                                                                   "counterfeit",
                                                                   "cultist",
                                                                   "dameanna",
                                                                   "damejosephine",
                                                                   "damemolly",
                                                                   "damenatalie",
                                                                   "damesylvia",
                                                                   "graverobber",
                                                                   "junkdealer",
                                                                   "mystic",
                                                                   "pillage",
                                                                   "rebuild",
                                                                   "rogue",
                                                                   "sirbailey",
                                                                   "sirdestry",
                                                                   "sirmichael",
                                                                   "sirvander",
                                                                   "altar",
                                                                   "huntinggrounds",
                                                                   "candlestickmaker",
                                                                   "stonemason",
                                                                   "stash",
                                                                   "doctor",
                                                                   "masterpiece",
                                                                   "advisor",
                                                                   "herald",
                                                                   "plaza",
                                                                   "taxman",
                                                                   "baker",
                                                                   "butcher",
                                                                   "journeyman",
                                                                   "merchantguild",
                                                                   "soothsayer",
                                                                   "coinoftherealm",
                                                                   "page",
                                                                   "peasant",
                                                                   "ratcatcher",
                                                                   "raze",
                                                                   "amulet",
                                                                   "caravanguard",
                                                                   "dungeon",
                                                                   "gear",
                                                                   "guide",
                                                                   "duplicate",
                                                                   "magpie",
                                                                   "messenger",
                                                                   "miser",
                                                                   "port",
                                                                   "ranger",
                                                                   "transmogrify",
                                                                   "artificer",
                                                                   "bridgetroll",
                                                                   "distantlands",
                                                                   "giant",
                                                                   "hauntedwoods",
                                                                   "lostcity",
                                                                   "relic",
                                                                   "royalcarriage",
                                                                   "storyteller",
                                                                   "swamphag",
                                                                   "treasuretrove",
                                                                   "winemerchant",
                                                                   "hireling",
                                                                   "soldier",
                                                                   "fugitive",
                                                                   "disciple",
                                                                   "teacher",
                                                                   "treasurehunter",
                                                                   "warrior",
                                                                   "hero",
                                                                   "champion",
                                                                   "alms",
                                                                   "borrow",
                                                                   "quest",
                                                                   "save",
                                                                   "scoutingparty",
                                                                   "travellingfair",
                                                                   "bonfire",
                                                                   "expedition",
                                                                   "ferry",
                                                                   "plan",
                                                                   "mission",
                                                                   "pilgrimage",
                                                                   "ball",
                                                                   "raid",
                                                                   "seaway",
                                                                   "trade",
                                                                   "lostarts",
                                                                   "training",
                                                                   "inheritance",
                                                                   "pathfinding",
                                                                   "copper",
                                                                   "curse",
                                                                   "estate",
                                                                   "silver",
                                                                   "duchy",
                                                                   "gold",
                                                                   "province"));
        if (cardList.contains(result)) {
            return result;
        } else if (cardList.contains(result.substring(0, 
        			result.length() - 1))) {
            return result.substring(0, result.length() - 1);
        }
        return null;

    }
}