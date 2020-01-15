package com.rbop.examples;

import java.util.*;

public class FixedLists {

    public static final Map<String, String> Punctuation;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put(" ", "\\u00A0\\u200e"); //things to replace with a single space
       // aMap.put(" ", "[-/']+"); //things to replace with a single space
        aMap.put("", "[.?:]+"); //things to replace with no space
        Punctuation = Collections.unmodifiableMap(aMap);
    }

    //MappedValues Column: [(A,B) (A1,B1) (A2,B2)] are used as follows:
    //for specific values in specific columns, replace value A with a mapped value B
    public static final Map<String, List<Pair<String,String>>> MappedValues;
    static {
        //first build list of pairings for first column
        List<Pair<String,String>> rocktypeSynonyms = new ArrayList<Pair<String,String>>();
        Pair<String, String> formPair1 = new Pair<String,String>();
        formPair1.setL("sands");formPair1.setR("Sandstone");rocktypeSynonyms.add(formPair1);
        Pair<String, String> formPair2 = new Pair<String,String>();
        formPair2.setL("carbonates");formPair2.setR("Carbonate");rocktypeSynonyms.add(formPair2);

        //build list of word replacement pairs for next column
        List<Pair<String,String>> countrySynonyms = new ArrayList<Pair<String,String>>();
        Pair<String, String> countryPair1 = new Pair<String,String>();
        Pair<String, String> countryPair2 = new Pair<String,String>();
        countryPair1.setL("USA");countryPair1.setR("United States");countrySynonyms.add(countryPair1);

        //build list of word replacement pairs for next column
        List<Pair<String,String>> countryregionSynonyms = new ArrayList<Pair<String,String>>();
        Pair<String, String> countryregionPair1 = new Pair<String,String>();
        Pair<String, String> countryregionPair2 = new Pair<String,String>();
        countryregionPair1.setL("Ghana Offshore");countryregionPair1.setR("Gulf of Guinea");countryregionSynonyms.add(countryregionPair1);


        //build list of word replacement pairs for next column
        List<Pair<String,String>> formationSynonyms = new ArrayList<Pair<String,String>>();
        Pair<String, String> formationPair1 = new Pair<String,String>();
        Pair<String, String> formationPair2 = new Pair<String,String>();
        formationPair1.setL("dolomites");formationPair1.setR("dolomite");formationSynonyms.add(formationPair1);


        //then add each list of pairs to the map
        Map<String, List<Pair<String, String>>> aMap = new HashMap<>();
        aMap.put("Rock_Type", rocktypeSynonyms);
        aMap.put("Country", countrySynonyms);
        aMap.put("Country_Region", countryregionSynonyms);
      //  aMap.put("Country_Region", countryregionSynonyms);
        MappedValues = Collections.unmodifiableMap(aMap);
    }

    public static final Map<String, String> WeirdChars;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("'", "[\\u0022\\u0060\\u00B4\\u2018\\u2019\\u201C\\u201D]+"); //quotes https://www.cl.cam.ac.uk/~mgk25/ucs/quotes.html
        aMap.put("-", "[\\u2014\\u2013\\u2212]+"); //em, en, minus, hyphen-minus
        aMap.put("", "[\\u200e]+"); //non-printing space in ‎Block‎ ‎211/29
        aMap.put(" ", "[\\u00A0]+"); //non-breaking whitespace in ‎Fletcher Field
        WeirdChars = Collections.unmodifiableMap(aMap);
    }

    public static final Map<String, List<String>> LegalValues;
    static {
        Map<String, List<String>> aMap = new HashMap<>();
        aMap.put("Geologic_Age", Arrays.asList(new String[]{"CISURALIAN","PALEOCENE","DANIAN","TITHONIAN","LEONARDIAN","WOLFCAMPIAN", "TRIASSIC", "PERMIAN", "MIOCENE", "JURASSIC", "CRETACEOUS", "TURONIAN", "SILURIAN", "PLIOCENE", "PENNSYLVANIAN", "PALEOZOIC", "PALEOGENE", "ORDOVICIAN", "OLIGOCENE", "NEOGENE", "MISSISSIPPIAN", "MIOCENE", "EOCENE", "DEVONIAN", "MESSIANIAN", "TERTIARY", "ALBIAN", "KIMMERIDGIAN", "GUADALUPIAN", "FURONGIAN", "GIVETIAN", "CENOMANIAN", "CARBONIFEROUS", "APTIAN", "BARREMIAN", "ALBIAN", "MESOZOIC", "PLEISTOCENE", "CENOZOIC", "CAMBRIAN"}));
        aMap.put("Rock_Type", Arrays.asList(new String[]{"IGNEOUS","METAMORPHIC","SEDIMENTARY","SANDSTONE", "SHALE", "CARBONATE", "TURBIDITE", "SILTSTONE", "LIMESTONE"}));
        aMap.put("Onshore_Offshore", Arrays.asList(new String[]{"ONSHORE", "OFFSHORE"}));
        aMap.put("Type", Arrays.asList(new String[]{"OIL", "GAS", "CONDENSATE", "HEAVY OIL","BITUMEN","COAL","COALBED METHANE"}));
        LegalValues = Collections.unmodifiableMap(aMap);
    }



    public static final Map<String, List<String>> LegalCanonicals;
    static {
        Map<String, List<String>> aMap = new HashMap<>();
      //  aMap.put("Formation", Arrays.asList(new String[]{"reservoir","formation","anticline"}));
        aMap.put("Formation", Arrays.asList(new String[]{"reservoir","formation","anticline","shale","sandstone","limestone","clastics","dolomite","group","bars","structure","trend","lime"}));
        aMap.put("Basin", Arrays.asList(new String[]{"graben","basin","terrace","shelf","depression","high","uplift"}));
        aMap.put("Well", Arrays.asList(new String[]{"well"}));
        aMap.put("Field", Arrays.asList(new String[]{"field","complex"}));
        //aMap.put("Block", Arrays.asList(new String[]{"block","permit"}));
        LegalCanonicals = Collections.unmodifiableMap(aMap);
    }


    public static final Map<String, List<String>> Synonyms;
    static {
        Map<String, List<String>> aMap = new HashMap<>();
        aMap.put("Field", Arrays.asList(new String[]{"field","prospect","project","acreage","Acreage","oil discovery","Oil Discovery"}));
        aMap.put("Formation", Arrays.asList(new String[]{"reservoir","formation","play","anticline","syncline","trend","member","structure","group","member","chalk"}));
        aMap.put("Basin", Arrays.asList(new String[]{"graben","basin","terrace","megabasin"}));
        aMap.put("Well", Arrays.asList(new String[]{"well"}));
        Synonyms = Collections.unmodifiableMap(aMap);
    }


    public static final Map<String, List<String>> OffshoreWords;
    static {
        Map<String, List<String>> aMap = new HashMap<>();
        aMap.put("Offshore_CountryRegion", Arrays.asList(new String[]{"Sea", "Bay", "Offshore", "Deepwater", "Ocean", "Atlantic", "Bight", "Channel", "Delta", "Strait", "Reef", "Shelf"}));
        aMap.put("Offshore_Basin", Arrays.asList(new String[]{"Sea", "Bay", "Offshore", "Deepwater", "Ocean", "Atlantic", "Bight", "Channel", "Delta", "Strait", "Reef", "Shelf"}));
        aMap.put("Offshore_Legals", Arrays.asList(new String[]{"Onshore","Offshore","Onshore;Offshore"}));
        OffshoreWords = Collections.unmodifiableMap(aMap);
    }

    public static final Map<String, List<String>> Regions;
    static {
        Map<String, List<String>> aMap = new HashMap<>();
        aMap.put("Asia", Arrays.asList(new String[]{"Afghanistan", "Azerbaijan", "Bangladesh", "Brunei", "Cambodia", "China", "India", "Indonesia", "Japan", "Kazakhstan", "Kyrgysztan", "Malaysia", "Mongolia", "Myanmar", "North Korea", "Pakistan", "Phillipines", "Russia", "South Korea", "Sri Lanka", "Taiwan", "Tajikistan", "Thailand", "Turkmenistan", "Uzbekistan", "Vietnam"}));
        aMap.put("North America", Arrays.asList(new String[]{"Belize", "Canada", "Costa Rica", "Cuba", "Dominican Republic", "Greenland","Grenada", "Guatemala", "Haiti", "Honduras", "Jamaica", "Mexico", "Nicaragua", "Panama", "Trinidad and Tobago", "United States"}));
        aMap.put("South America", Arrays.asList(new String[]{"Argentina", "Bolivia", "Brazil", "Chile", "Colombia", "Ecuador", "Falkland Islands", "Guyana", "Peru", "Suriname", "Uruguay", "Venezuela"}));
        aMap.put("Europe", Arrays.asList(new String[]{"Albania", "Austria", "Belarus", "Belgium", "Bosnia and Herzegovinia", "Croatia", "Czech Republic", "Denmark", "Estonia", "France", "Germany", "Greece", "Hungary", "Iceland", "Ireland", "Italy", "Latvia", "Lithuania", "Moldova", "Netherlands", "Norway", "Poland", "Portugal", "Romania", "Russia", "Serbia", "Slovakia", "Slovenia", "Spain", "Switzerland", "Ukraine", "United Kingdom"}));
        aMap.put("Africa", Arrays.asList(new String[]{"Angola", "Botswana", "Cameroon", "Central African Republic", "Chad", "Cote d'Ivoire", "Democratic Republic of the Congo", "Equatorial Guinea", "Ethiopia", "Gabon", "Ghana", "Ivory Coast", "Kenya", "Liberia", "Madagascar", "Mali", "Mauritania", "Mozambique", "Namibia", "Nigeria", "Republic of Niger", "Republic of the Congo", "Sao Tome and Principe", "Senegal", "South Africa", "South Sudan", "Sudan", "Tanzania", "Uganda", "West Africa", "Zambia"}));
        aMap.put("Oceania", Arrays.asList(new String[]{"Australia", "Fiji", "New Caledonia", "New Zealand", "Papua New Guinea", "Solomon Islands", "Timor Leste"}));
        aMap.put("Africa|Middle East", Arrays.asList(new String[]{"Algeria", "Egypt", "Libya", "Morocco", "Somalia", "Tunisia"}));
        aMap.put("Antarctica", Arrays.asList(new String[]{"Antarctica"}));
        aMap.put("Europe|Middle East", Arrays.asList(new String[]{"Cyprus","Malta"}));
        aMap.put("Asia|Middle East", Arrays.asList(new String[]{"Bahrain", "Iran", "Iraq", "Israel", "Jordan", "Kuwait", "Lebanon", "Oman", "Qatar", "Saudi Arabia", "Syria", "Turkey", "UAE", "West Bank and Gaza", "Yemen"}));
        Regions = Collections.unmodifiableMap(aMap);
    }

    public static final Map<String, List<String>> Facilities;
    static {
        Map<String, List<String>> aMap = new HashMap<>();
        aMap.put("Facility Type", Arrays.asList(new String[]{"LNG Project","Gas Plant", "Gas Refinery", "Oil Refinery", "Pipeline","Platform","Liquid Natural Gas Project","LPG Project"}));
        aMap.put("Columns", Arrays.asList(new String[]{"Mapped_Term","Field"}));
        Facilities = Collections.unmodifiableMap(aMap);
    }

    public static final List<String> Smallwords = Arrays.asList(new String[]{"and", "of", "in", "de", "do", "al","a","the"}); //this is for caps
    public static final List<String> Replacers =  Arrays.asList(new String[]{"LNG project","discovery","project","Project","prospect","production sharing contract","basin", "field", "well", "block","d'Ivoire"});


    public static final Map<String, List<String>> CountryRegions;
    static {
        Map<String, List<String>> aMap = new HashMap<>();
        aMap.put("Algeria", Arrays.asList(new String[]{"Adrar Province", "Batna Province", "Central Algeria", "Eastern Algeria", "Illizi Province", "Laghouat Province", "M'Sila Province", "Mediterranean Sea", "Northern Algeria", "Ouargla Province", "Relizane Province", "Tamanrasset Province", "Tebessa Province", "Western Algeria"}));
        aMap.put("Afghanistan", Arrays.asList(new String[]{}));
        aMap.put("Albania", Arrays.asList(new String[]{}));
        aMap.put("Angola", Arrays.asList(new String[]{"Angola Offshore"}));
        aMap.put("Antarctica", Arrays.asList(new String[]{"Antarctica Offshore","South Indian Ocean|Kerguelen Plateau","South Atlantic Ocean|Weddell Sea","South Indian Ocean","Victoria Land","South Atlantic Ocean"}));
        aMap.put("Argentina", Arrays.asList(new String[]{"Argentina Offshore|South Atlantic Ocean", "Bahia Province", "Buenos Aires Province", "Chubut Province", "Jujuy Province", "La Pampa Province", "Loma El Divisadero Property", "Mendoza Province", "Neuquen Province", "Patagonia", "Patagonia Offshore", "Puesto Morales", "Rio Negro Province", "Salta Province", "Santa Cruz Province", "Tierra del Fuego", "Tierra del Fuego Offshore"}));
        aMap.put("Australia", Arrays.asList(new String[]{"New South Wales", "New South Wales Offshore", "Northern Territory", "Northern Territory Offshore", "Queensland", "Queensland Offshore", "South Australia", "South Australia Offshore", "South Australia|Indian Ocean", "South Pacific Ocean", "Tasmania", "Tasmania Offshore", "Timor Sea", "Victoria", "Victoria|Bass Strait", "Western Australia", "Western Australia Offshore", "Western Australia|Indian Ocean", "Western Australia|North West Shelf", "Western Australia|Timor Sea"}));
        aMap.put("Austria", Arrays.asList(new String[]{}));
        aMap.put("Azerbaijan", Arrays.asList(new String[]{"Apsheron Peninsula", "Caspian Sea"}));
        aMap.put("Bahrain", Arrays.asList(new String[]{}));
        aMap.put("Bangladesh", Arrays.asList(new String[]{"Bangladesh Offshore"}));
        aMap.put("Belarus", Arrays.asList(new String[]{}));
        aMap.put("Belgium", Arrays.asList(new String[]{"North Sea"}));
        aMap.put("Bolivia", Arrays.asList(new String[]{"Chuquisaca Department", "Cochabamba Department", "Santa Cruz Department", "Tarija Department"}));
        aMap.put("Bosnia and Herzegovinia", Arrays.asList(new String[]{}));
        aMap.put("Botswana", Arrays.asList(new String[]{}));
        aMap.put("Brazil", Arrays.asList(new String[]{"Alagoas State", "Alagoas State Offshore", "Amapa State", "Amapa State Offshore", "Amazonas State", "Bahia State", "Bahia State Offshore", "Brazil Offshore", "Brazil Offshore|Atlantic Ocean", "Ceara State", "Ceara State Offshore", "Espirito Santo State", "Espírito Santo State", "Espirito Santo State Offshore", "Espírito Santo State Offshore", "Maranhao State", "Maranhao State Offshore", "Mato Grosso State", "Minas Gerais State", "Para State", "Paraiba State", "Paraiba State Offshore", "Pernambuco State", "Pernambuco State Offshore", "Piaui State", "Rio de Janeiro State", "Rio de Janeiro State Offshore", "Rio Grande do Norte State", "Rio Grande do Norte State Offshore", "Sao Paulo State", "São Paulo State", "Sao Paulo State Offshore", "São Paulo State Offshore", "Sergipe State", "Sergipe State Offshore"}));
        aMap.put("Brunei", Arrays.asList(new String[]{"Brunei Offshore"}));
        aMap.put("Bulgaria", Arrays.asList(new String[]{"Black Sea"}));
        aMap.put("Cambodia", Arrays.asList(new String[]{}));
        aMap.put("Cameroon", Arrays.asList(new String[]{"Cameroon Offshore"}));
        aMap.put("Canada", Arrays.asList(new String[]{"Alberta", "Arctic Ocean", "British Columbia", "British Columbia Offshore", "British Columbia|North Pacific Ocean", "Labrador Sea", "Manitoba", "New Brunswick", "Newfoundland", "Newfoundland Offshore", "North Atlantic Ocean", "Northwest Territories", "Nova Scotia", "Nova Scotia Offshore", "Nunavut", "Nunavut Offshore", "Ontario", "Quebec", "Quebec Offshore", "Saskatchewan", "Yukon"}));
        aMap.put("Chad", Arrays.asList(new String[]{"Southern Chad"}));
        aMap.put("Chile", Arrays.asList(new String[]{"Magellanes Region XXII", "Tierra del Fuego"}));
        aMap.put("China", Arrays.asList(new String[]{"Anhui Province", "Bohai Bay", "Chongqing Province", "East China Sea", "Fuijan", "Gansu Province", "Guangdong Province", "Guangxi Province", "Hainan Province", "Hebei Province", "Heilongjiang Province", "Henan Province", "Hunan Province", "Inner Mongolia", "Jiangsu Province", "Jiangxi Province", "Jilin Province", "Liadong Bay", "Liaoning Province", "Mongolia", "Ningxia Hui Autonomous Region", "Northeast China", "Northwest China", "Qinghai Province", "Shaanxi Province", "Shandong Province", "Sichuan Province", "South China Sea", "South China Sea|Beibu Gulf", "South China Sea|Ledong Sag", "Tianjin Province", "Tibet Autonomous Region", "Tibetan Plateau", "Xinjiang Uyghur Autonomous Region", "Yellow Sea", "Yunnan Province"}));
        aMap.put("Colombia", Arrays.asList(new String[]{"Antioquia Department", "Arauca Department", "Bolivar Department", "Boyaca Department", "Casanare Department", "Cesar Department", "Colombia Offshore|Caribbean Sea", "Colombia Offshore|Pacific Ocean", "Cundinamarca Department", "Huila Department", "La Guajira Department", "Magdalena Department", "Meta Department", "Middle Magdalena Valley", "Norte de Santander Department", "Putamayo Department", "Sucre Department", "Tolima Department", "Upper Magdalena Valley"}));
        aMap.put("Costa Rica", Arrays.asList(new String[]{}));
        aMap.put("Cote d'Ivoire", Arrays.asList(new String[]{"Cote d'Ivoire Offshore"}));
        aMap.put("Croatia", Arrays.asList(new String[]{}));
        aMap.put("Cuba", Arrays.asList(new String[]{}));
        aMap.put("Cyprus", Arrays.asList(new String[]{"Mediterranean Sea"}));
        aMap.put("Czech Republic", Arrays.asList(new String[]{}));
        aMap.put("Democratic Republic of the Congo", Arrays.asList(new String[]{"Democratic Republic of the Congo Offshore"}));
        aMap.put("Denmark", Arrays.asList(new String[]{"North Sea"}));
        aMap.put("Dominican Republic", Arrays.asList(new String[]{"Caribbean Sea"}));
        aMap.put("Ecuador", Arrays.asList(new String[]{"Amazon", "Amazon|Orellana Province", "Gulf of Guayaquil", "Napo Province", "Nueva Loja Province", "Orellana Province", "Oriente Area", "Pastaza Province", "Sucumbíos Province",}));
        aMap.put("Egypt", Arrays.asList(new String[]{"Faiyum Governorate", "Gulf of Suez", "Mediterranean Sea", "Nile Delta", "North Sinai Governorate", "Red Sea Governorate", "South Central Desert", "South Sinai Governorate", "Western Desert"}));
        aMap.put("Ethiopia", Arrays.asList(new String[]{}));
        aMap.put("Equatorial Guinea", Arrays.asList(new String[]{"Equatorial Guinea Offshore"}));
        aMap.put("Falkland Islands", Arrays.asList(new String[]{"Falkland Islands Offshore"}));
        aMap.put("Fiji", Arrays.asList(new String[]{"Fiji Offshore", "South Pacific Ocean"}));
        aMap.put("France", Arrays.asList(new String[]{"Grand Est", "Mediterranean Sea", "Nouvelle-Aquitaine"}));
        aMap.put("Gabon", Arrays.asList(new String[]{"Gabon Offshore"}));
        aMap.put("Germany", Arrays.asList(new String[]{"Bavaria", "Lower Saxony", "North Sea"}));
        aMap.put("Ghana", Arrays.asList(new String[]{"Gulf of Guinea"}));
        aMap.put("Greece", Arrays.asList(new String[]{"Aegean Sea", "Mediterranean Sea"}));
        aMap.put("Greenland", Arrays.asList(new String[]{"Arctic Ocean", "Melville Bay", "North Atlantic Ocean|Irminger Sea", "North Greenland", "Northeast Greenland", "Southern West Greenland", "West Greenland"}));
        aMap.put("Grenada", Arrays.asList(new String[]{"Caribbean Sea"}));
        aMap.put("Guatemala", Arrays.asList(new String[]{"Chiapas", "Guatemala Offshore|North Pacific Ocean", "Tabasco"}));
        aMap.put("Guyana", Arrays.asList(new String[]{"Guyana Offshore"}));
        aMap.put("Haiti", Arrays.asList(new String[]{"Caribbean Sea"}));
        aMap.put("Honduras", Arrays.asList(new String[]{}));
        aMap.put("Hungary", Arrays.asList(new String[]{}));
        aMap.put("Iceland", Arrays.asList(new String[]{"North Atlantic Ocean", "North Atlantic Ocean|Irminger Sea"}));
        aMap.put("India", Arrays.asList(new String[]{"Amalapuram", "Amalapuram Offshore", "Andhra Pradesh", "Andhra Pradesh Offshore", "Arabian Sea", "Arunachal Pradesh", "Assam", "Assam|Diburugarh District", "Chhattisgarh", "Gujarat", "Gujarat Offshore", "Gulf of Cambay", "India Offshore", "Jharkhand", "Madhya Pradesh", "Rajasthan", "Surma Valley", "Tamil Nadhu", "Tamil Nadhu Offshore", "Tripura", "Western India", "Western India Offshore"}));
        aMap.put("Indonesia", Arrays.asList(new String[]{"Aceh", "Arafura Sea", "East Java", "East Java Offshore", "East Kalimantan", "East Kalimantan Offshore", "Jambi", "Java", "Java Sea", "Kalimantan", "Kalimantan Offshore", "Malaku|Seram Island", "Natuna Sea", "North Sumatra", "North Sumatra Offshore", "Northeast Kalimantan", "Northwest Java Sea", "Riau", "Savu Sea", "South China Sea", "South Natuna Sea", "South Sumatra", "Straits of Malacca", "Sulawesi", "Sulawesi|Gorontalo Bay", "Sulu Sea", "Sumatra", "West Java", "West Kalimantan", "West Papua", "West Papua|Bintuni Bay", "West Sumatra"}));
        aMap.put("Iran", Arrays.asList(new String[]{"Arabian Gulf", "Ardabil Province", "Bushehr Province", "Fars Province", "Golestan Province", "Hormozgan Province", "Ilam Province", "Kermanshah Province", "Khuzestan Province", "Kohgiluyeh and Boyer-Ahmad Province", "Lorestan Province", "Mazandaran Province", "Qom Province", "Razavi Khorasan Province"}));
        aMap.put("Iraq", Arrays.asList(new String[]{"Al Anbar Governorate", "Baghdad Governorate", "Basra Governorate", "Dhi Qar Governorate", "Diyala Governorate", "Karbala Governorate", "Kirkuk Governorate", "Kurdistan", "Maysan Governorate", "Muthanna Governorate", "Nineveh Governorate", "Saladin Governorate", "Wasit Governorate"}));
        aMap.put("Ireland", Arrays.asList(new String[]{"Atlantic Ocean", "Celtic Sea", "Donegal Offshore", "Ireland Offshore", "Irish Sea"}));
        aMap.put("Israel", Arrays.asList(new String[]{"Israel Offshore"}));
        aMap.put("Italy", Arrays.asList(new String[]{"Molise","Abruzzi","Adriatic Sea", "Bologna", "Emilia Romagna", "Ionian Sea", "Lombardy", "Mediterranean Sea", "Mediterranean Sea|Tyrrhenian Sea", "Milan", "Sicilian Channel", "Sicily"}));
        aMap.put("Ivory Coast", Arrays.asList(new String[]{"Ivory Coast Offshore"}));
        aMap.put("Jamaica", Arrays.asList(new String[]{}));
        aMap.put("Japan", Arrays.asList(new String[]{"Hokkaido Island", "Honshu Island", "Honshu Island|Akita Prefecture", "Honshu Island|Niigata Prefecture", "Honshu Island|Mie Prefecture Offshore", "Japan Offshore", "Japan Offshore|North Pacific Ocean", "Kyushu Island", "Yamagata Prefecture"}));
        aMap.put("Jordan", Arrays.asList(new String[]{"Eastern Jordan", "Wadi al Azraq"}));
        aMap.put("Kenya", Arrays.asList(new String[]{"Kenya Offshore","Northern Kenya"}));
        aMap.put("Kazakhstan", Arrays.asList(new String[]{"Aktobe Province", "Almaty Province", "Atyrau Province", "Atyrau Province|Caspian Sea", "Caspian Sea", "Kyzylorda Province", "Manghystau Province", "Mangystau Province|Caspian Sea", "North Kazakhstan Province", "West Kazakhstan Province"}));
        aMap.put("Kuwait", Arrays.asList(new String[]{"Ahmadi Governorate", "Asimah Governorate", "Farwaniya Governorate", "Jafra Governorate", "Jahra Governorate", "North Kuwait", "Northwest Kuwait", "Saudi Arabia - Kuwait Neutral Zone|Arabian Gulf"}));
        aMap.put("Kyrgyzstan", Arrays.asList(new String[]{}));
        aMap.put("Laos", Arrays.asList(new String[]{}));
        aMap.put("Latvia", Arrays.asList(new String[]{}));
        aMap.put("Lebanon", Arrays.asList(new String[]{}));
        aMap.put("Liberia", Arrays.asList(new String[]{}));
        aMap.put("Libya", Arrays.asList(new String[]{"Al Wahat District", "Jabal al Gharbi District", "Jufra District", "Libya Offshore", "Murzuq District", "Nalut District", "Nuqat al Khams District", "Sirte District", "Sirte District Offshore", "Wadi al Hayaa District"}));
        aMap.put("Lithuania", Arrays.asList(new String[]{}));
        aMap.put("Madagascar", Arrays.asList(new String[]{"Madagascar Offshore"}));
        aMap.put("Malaysia", Arrays.asList(new String[]{"Bay of Bengal", "Gulf of Thailand", "Malaysia Offshore", "Sabah Offshore", "Sarawak", "Sarawak Offshore", "Terengganu", "Terengganu Offshore"}));
        aMap.put("Mali", Arrays.asList(new String[]{}));
        aMap.put("Malta", Arrays.asList(new String[]{"Mediterranean Sea"}));
        aMap.put("Mauritania", Arrays.asList(new String[]{"Mauritania Offshore"}));
        aMap.put("Mexico", Arrays.asList(new String[]{"Baja California", "Baja California Offshore", "Baja California Sur", "Campeche", "Chiapas", "Chihuahua", "Coahuila", "Durango", "Guerrero", "Gulf of California", "Gulf of Mexico", "Gulf of Mexico|Bay of Campeche", "Michoacan", "Nayarit", "Nayarit Offshore", "Nuevo Leon", "Tabasco", "Tabasco Offshore", "Tamaulipas", "San Luis Potosi", "Hidalgo", "Puebla", "Veracruz", "Tlaxcala"}));
        aMap.put("Moldova", Arrays.asList(new String[]{}));
        aMap.put("Mongolia", Arrays.asList(new String[]{}));
        aMap.put("Morocco", Arrays.asList(new String[]{"Morocco Offshore", "Morocco Offshore|North Atlantic Ocean"}));
        aMap.put("Mozambique", Arrays.asList(new String[]{"Mozambique Offshore", "Mozambique Offshore|Cape Delgado"}));
        aMap.put("Myanmar", Arrays.asList(new String[]{"Andaman Sea", "Andaman Sea|Gulf of Martaban", "Ayeyarwady Region", "Bago Region", "Magway Region", "Mandalay Region", "Myanmar Offshore", "Yangon Region"}));
        aMap.put("Namibia", Arrays.asList(new String[]{"Namibia Offshore"}));
        aMap.put("Netherlands", Arrays.asList(new String[]{"Friesland", "North Sea", "Waddenzee"}));
        aMap.put("New Caledonia", Arrays.asList(new String[]{"South Pacific Ocean"}));
        aMap.put("New Zealand", Arrays.asList(new String[]{"North Island", "North Island Offshore", "North Island|Hawke's Bay", "South Island", "South Island Offshore", "South Island|South Pacific Ocean", "South Pacific Ocean", "Tasman Sea"}));
        aMap.put("Nigeria", Arrays.asList(new String[]{"Akwa Ibom State", "Bayelsa State", "Gulf of Guinea|Bight of Bonny", "Delta State", "Gulf of Guinea", "Imo State", "Rivers State"}));
        aMap.put("North Korea", Arrays.asList(new String[]{}));
        aMap.put("Norway", Arrays.asList(new String[]{"Arctic Ocean", "Barents Sea", "North Sea", "Barents Sea", "Norwegian Sea", "Svalbard"}));
        aMap.put("Oman", Arrays.asList(new String[]{"Ad Dakhiliyah Governorate", "Ad Dhahirah Governorate", "Al Buraymi Governorate", "Al Sharqiyah Governorate", "Al Wusta Governorate", "Arabian Sea", "Ash Sharqiyah North Governorate", "Central Oman", "Dhofar Governorate", "Oman Offshore", "South Oman", "Strait of Hormuz"}));
        aMap.put("Pakistan", Arrays.asList(new String[]{"Balochistan Province", "Balochistan Province|Dera Bugti District", "Balochistan Province|Zhob District", "Khyber Pakhtunkhwa Province", "Khyber Pakhtunkhwa Province|Karak District", "Khyber Pakhtunkhwa Province|Peshawar District", "Pakistan Offshore", "Punjab Province", "Punjab Province|Chakwal District", "Punjab Province|D.G. Khan District", "Punjab Province|Rawalpindi District", "Sindh Province", "Sindh Province|Dadu District", "Sindh Province|Ghotki District", "Sindh Province|Khaipur District", "Sindh Province|Sanghar District"}));
        aMap.put("Palestine", Arrays.asList(new String[]{"Gaza"}));
        aMap.put("Panama", Arrays.asList(new String[]{"Panama Offshore|Pacific Ocean"}));
        aMap.put("Papua New Guinea", Arrays.asList(new String[]{"Bismarck Sea", "Gulf of Papua", "Gulf Province", "North Highlands", "Solomon Islands", "Solomon Islands Offshore", "Solomon Sea", "Southern Highlands","Western Province"}));
        aMap.put("Paraguay", Arrays.asList(new String[]{"Chaco Region"}));
        aMap.put("Peru", Arrays.asList(new String[]{"Cusco Department", "Northwest Peru Offshore", "Peru Offshore"}));
        aMap.put("Philippines", Arrays.asList(new String[]{"Albay Province", "Leyte Province", "Pacific Ocean", "Philippines Offshore", "West Philippine Sea"}));
        aMap.put("Poland", Arrays.asList(new String[]{"Lublin Province", "North Sea", "Western Poland"}));
        aMap.put("Portugal", Arrays.asList(new String[]{"Portugal Offshore|Gulf of Cadiz"}));
        aMap.put("Qatar", Arrays.asList(new String[]{"Arabian Gulf"}));
        aMap.put("Republic of Niger", Arrays.asList(new String[]{}));
        aMap.put("Republic of the Congo", Arrays.asList(new String[]{"Republic of the Congo Offshore"}));
        aMap.put("Romania", Arrays.asList(new String[]{"Black Sea", "Bukovina Region", "Northern Romania", "Oltenia", "Romania Offshore", "Saros-Sighisoara Region", "Suceava"}));
        aMap.put("Russia", Arrays.asList(new String[]{"Arctic Ocean", "Barents Sea", "Barents Sea|Kara Sea", "Barents Sea|Pechora Sea", "Bering Sea", "Central Federal District", "Central Federal District|Tula Oblast", "Far Eastern Federal District", "Far Eastern Federal District|Chukotka Autonomous Okrug", "Far Eastern Federal District|Kamchatka Krai", "Far Eastern Federal District|Kamchatka Peninsula", "Far Eastern Federal District|Khabarovsk Krai", "Far Eastern Federal District|Northern Sakhalin", "Far Eastern Federal District|Primorsky Krai", "Far Eastern Federal District|Sakha Republic (Yakutia)", "Far Eastern Federal District|Sakhalin Island", "Far Eastern Federal District|Sakhalin Island Offshore", "Far Eastern Federal District|Sea of Okhotsk", "North Caucasian Federal District", "North Caucasian Federal District|Chechnya", "North Caucasian Federal District|Dagestan", "North Caucasian Federal District|Dagestan|Caspian Sea", "North Caucasian Federal District|Ingushetia", "North Caucasian Federal District|Stavropol Krai", "Northwestern Federal District", "Northwestern Federal District|Kaliningrad Oblast", "Northwestern Federal District|Kaliningrad Oblast|Baltic Sea", "Northwestern Federal District|Komi Republic", "Northwestern Federal District|Nenets Autonomous Okrug", "Northwestern Federal District|Murmansk Oblast", "Northwestern Federal District|Nenets Autonomous Okrug|Zapolyarny District", "Northwestern Federal District|Novgorod Oblast", "Sea of Okhotsk", "Siberian Federal District", "Siberian Federal District|Buryatia", "Siberian Federal District|Irkutsk Oblast", "Siberian Federal District|Kemerovo Oblast", "Siberian Federal District|Khakassia", "Siberian Federal District|Krasnoyarsk Krai","Siberian Federal District|Krasnoyarsk Krai|Laptev Sea", "Siberian Federal District|Krasnoyarsk Krai|Evenkia", "Siberian Federal District|Tomsk Oblast", "Siberian Federal District|Zabaykalsky Krai", "Southern Federal District", "Southern Federal District|Adygea", "Southern Federal District|Astrakhan Oblast", "Southern Federal District|Astrakhan Oblast|Caspian Sea", "Southern Federal District|Kalmykia", "Southern Federal District|Kalmykia|Caspian Sea", "Southern Federal District|Krasnodar Krai", "Southern Federal District|Volgograd Oblast", "Ural Federal District", "Ural Federal District|Khanty-Mansi Autonomous Okrug", "Ural Federal District|Khanty-Mansi Autonomous Okrug|Novosibirsk Oblast", "Ural Federal District|Tyumen Oblast", "Ural Federal District|Yamalo-Nenets Autonomous Okrug", "Ural Federal District|Yamalo-Nenets Autonomous Okrug|Gulf of Ob", "Ural Federal District|Yamalo-Nenets Autonomous Okrug|Krasnoselkupsky District", "Ural Federal District|Yamalo-Nenets Autonomous Okrug|Purovsky District", "Ural Federal District|Yamalo-Nenets Autonomous Okrug|Taz Bay", "Ural Federal District|Yamalo-Nenets Autonomous Okrug|Tazovksy District", "Volga Federal District", "Volga Federal District|Bashkortostan", "Volga Federal District|Orenburg Oblast", "Volga Federal District|Perm Krai", "Volga Federal District|Samara Oblast", "Volga Federal District|Saratov Oblast", "Volga Federal District|Saratov Volgograd Area","Volga Federal District|Tatarstan", "Volga Federal District|Udmurtia"}));
        aMap.put("Sao Tome and Principe", Arrays.asList(new String[]{"Gulf of Guinea"}));
        aMap.put("Saudi Arabia", Arrays.asList(new String[]{"Arabian Gulf", "Eastern Province", "Eastern Province|Al-Ahsa Governorate", "Eastern Province|Rub al Khali", "Red Sea", "Riyadh Province", "Saudi Arabia - Kuwait Neutral Zone", "Tabuk Province", "Tabuk Province|Midyan Peninsula"}));
        aMap.put("Senegal", Arrays.asList(new String[]{"Senegal Offshore"}));
        aMap.put("Serbia", Arrays.asList(new String[]{}));
        aMap.put("Slovakia", Arrays.asList(new String[]{}));
        aMap.put("Slovenia", Arrays.asList(new String[]{}));
        aMap.put("Solomon Islands", Arrays.asList(new String[]{"South Pacific Ocean"}));
        aMap.put("Somalia", Arrays.asList(new String[]{"Somalia Offshore|Indian Ocean"}));
        aMap.put("South Africa", Arrays.asList(new String[]{"South Africa Offshore", "Indian Ocean"}));
        aMap.put("South Korea", Arrays.asList(new String[]{"Sea of Japan"}));
        aMap.put("Spain", Arrays.asList(new String[]{"Belearic Sea", "Mediterranean Sea"}));
        aMap.put("Sri Lanka", Arrays.asList(new String[]{"Sri Lanka Offshore"}));
        aMap.put("Suriname", Arrays.asList(new String[]{"Suriname Offshore"}));
        aMap.put("Switzerland", Arrays.asList(new String[]{}));
        aMap.put("Syria", Arrays.asList(new String[]{"Palmyra Region"}));
        aMap.put("Taiwan", Arrays.asList(new String[]{"Taiwan Offshore"}));
        aMap.put("Tajikistan", Arrays.asList(new String[]{}));
        aMap.put("Tanzania", Arrays.asList(new String[]{"Tanzania Offshore"}));
        aMap.put("Thailand", Arrays.asList(new String[]{"Chiang Mai Province", "Gulf of Thailand", "Kamphaeng Phet Province", "Petchabun Province"}));
        aMap.put("Timor Leste", Arrays.asList(new String[]{}));
        aMap.put("Trinidad and Tobago", Arrays.asList(new String[]{"Trinidad", "Trinidad Offshore"}));
        aMap.put("Tunisia", Arrays.asList(new String[]{"Eastern Algeria", "Northern Algeria", "Northern Tunisia", "Southern Tunisia", "Sud des Chotts", "Tunisia Offshore", "Tunisia Offshore|Kerkennah Island"}));
        aMap.put("Turkey", Arrays.asList(new String[]{"Adıyaman", "Black Sea", "Mediterranean Sea"}));
        aMap.put("Turkmenistan", Arrays.asList(new String[]{"Caspian Sea"}));
        aMap.put("UAE", Arrays.asList(new String[]{"Abu Dhabi", "Abu Dhabi Offshore", "Dubai", "Dubai Offshore", "Sharjah", "Sharjah Offshore"}));
        aMap.put("Uganda", Arrays.asList(new String[]{"Western Region", "Western Region|Hoima District"}));
        aMap.put("Ukraine", Arrays.asList(new String[]{"Chernihiv Oblast", "Kharkiv Oblast", "Krasnodar Territory", "Poltava Oblast"}));
        aMap.put("United Kingdom", Arrays.asList(new String[]{"Atlantic Margin", "Britsol Channel", "Celtic Sea", "England", "England|Dorset", "England|East Midlands", "England|Gloucestershire", "England|Isle of Wight", "England|Kent", "England|Lancashire", "England|North Yorkshire", "England|Staffordshire", "England|Surrey", "England|West Sussex", "England|Yorkshire", "English Channel", "Irish Sea", "Isle of Wight", "Isle of Wight Offshore", "North Sea"}));
        aMap.put("United States", Arrays.asList(new String[]{"Alabama", "Alaska", "Alaska|Bering Sea", "Alaska|Chukchi Sea", "Alaska|Kenai Peninsula", "Alaska|North Pacific Ocean", "Alaska|Prudhoe Bay", "Arizona", "Arkansas", "California", "California|North Pacific Ocean", "Colorado", "Florida", "Georgia", "Gulf of Maine", "Gulf of Mexico", "Idaho", "Illinois", "Indiana", "Kansas", "Kentucky", "Louisiana", "Louisiana Offshore", "Louisiana|Terrebonne Bay", "Louisiana|Timbalier Bay", "Maine", "Massachusetts", "Michigan", "Mississippi", "Montana", "Nebraska", "Nevada", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Virginia", "Washington", "West Virginia", "Wyoming"}));
        aMap.put("Uruguay", Arrays.asList(new String[]{"Uruguay Offshore"}));
        aMap.put("Uzbekistan", Arrays.asList(new String[]{"Karakalpakstan", "Namangan Province", "Qashqadaryo Province"}));
        aMap.put("Venezuela", Arrays.asList(new String[]{"Anzoategui State", "Apure State", "Barinas State", "Caribbean Sea", "Caribbean Sea|Gulf of Venezuela", "Falcon State", "Guarico State", "Gulf of Paria", "Gulf of Venezuela", "Lake Maracaibo", "Mariscal Sucre Offshore", "Monagas State", "Orinoco Oil Belt", "Portuguesa State", "Punta de Mata District", "Southwest Venezuela", "Tachira State", "Trujillo State", "Zulia State"}));
        aMap.put("Vietnam", Arrays.asList(new String[]{"South China Sea"}));
        aMap.put("West Africa", Arrays.asList(new String[]{}));
        aMap.put("West Bank and Gaza", Arrays.asList(new String[]{"Gaza Offshore"}));
        aMap.put("Yemen", Arrays.asList(new String[]{"Hadhramaut Governorate", "Ma'rib Governorate","Sana'a Governorate", "Shabwah Governorate"}));
        aMap.put("Zambia", Arrays.asList(new String[]{}));
        CountryRegions = Collections.unmodifiableMap(aMap);
    }
}