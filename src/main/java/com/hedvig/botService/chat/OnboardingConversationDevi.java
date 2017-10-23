package com.hedvig.botService.chat;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import com.hedvig.botService.serviceIntegration.MemberService;
import com.hedvig.botService.serviceIntegration.BankIdAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.botService.enteties.*;

public class OnboardingConversationDevi extends Conversation {

    private static Logger log = LoggerFactory.getLogger(OnboardingConversation.class);
    private static DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final MemberService memberService;


    private String emoji_smile = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x81}, Charset.forName("UTF-8"));
    private String emoji_hand_ok = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x91, (byte)0x8C}, Charset.forName("UTF-8"));
    private String emoji_closed_lock_with_key = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x94, (byte)0x90}, Charset.forName("UTF-8"));
    private String emoji_postal_horn = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x93, (byte)0xAF}, Charset.forName("UTF-8"));
    private String emoji_school_satchel = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x8E, (byte)0x92}, Charset.forName("UTF-8"));
    private String emoji_mag = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x94, (byte)0x8D}, Charset.forName("UTF-8"));
    private String emoji_revlolving_hearts = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x92, (byte)0x9E}, Charset.forName("UTF-8"));
    private String emoji_tada = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x8E, (byte)0x89}, Charset.forName("UTF-8"));
    private String emoji_thumbs_up = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x91, (byte)0x8D}, Charset.forName("UTF-8"));
    private String emoji_hug = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0xA4, (byte)0x97}, Charset.forName("UTF-8"));

    public OnboardingConversationDevi(MemberChat mc, UserContext uc, MemberService memberService) {
        super("onboarding", mc, uc);
        this.memberService = memberService;
        // TODO Auto-generated constructor stub

        Image testImage = new Image("http://www.apa.org/Images/insurance-title-image_tcm7-198694.jpg",730,330);

        createMessage("message.onboardingstart",
                new MessageBodySingleSelect(emoji_smile + " Hej, jag heter Hedvig!\n\fFint att ha dig här\n\fJag är en försäkringsbot så låt mig visa vad jag gör!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ge mig ett försäkringsförslag", "message.forslagstart"));
                            add(new SelectOption("Visa mig", "message.cad"));
                            add(new SelectOption("Jag är redan medlem", "message.medlem"));
                        }}
                ), "bike");

        createMessage("message.medlem",
                new MessageBodySingleSelect("Välkommen tillbaka "+ emoji_hug +"\n\n Ett snabbt BankID-inlogg bara, sen är du inne i appen igen",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Logga in", "message.bankidja"));
                        }}
                ));

        createMessage("message.forslagstart",
                new MessageBodySingleSelect(emoji_hand_ok + "\n\nDå sätter vi igång\n\nAllt du svarar är så klart i säkert förvar hos mig "+emoji_closed_lock_with_key+"\n\nBor du i lägenhet eller eget hus?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Lägenhet", "message.lagenhet"));
                            add(new SelectOption("Eget hus", "message.hus"));
                        }}
                ), "bike");

        createMessage("message.lagenhet",
                new MessageBodySingleSelect("Toppen\n\nLogga in med ditt BankID så kan vi snabbspola fram några frågor!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag loggar in", "message.bankid.start"));
                            add(new SelectOption("Jag har inget BankID", "message.manuellpersonnr"));
                        }}
                ));

        createMessage("message.bankid.start",
                new MessageBodySingleSelect("Då måste jag fråga ifall du har bankID på den enheten du använder nu?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Klart jag har, det är ju ändå 2017", "message.bankid.autostart.send"));
                            add(new SelectOption("Nej det har jag inte", "message.bankid.start.manual"));
                        }}
                ));

        createMessage("message.bankid.start.manual",
                new MessageBodyNumber("Om du anger ditt personnumer så får du använda bankId på din andra enhet " + emoji_smile
                ));


        createMessage("message.bankid.error",
                new MessageBodySingleSelect("Något blev fel när jag försökte kontakta min vän på BankId?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Försök igen", "message.bankid.start"));
                            add(new SelectOption("Hoppa över", "message.bankidja"));
                        }}
                ));

        createMessage("message.bankid.autostart.send",
                new MessageBodySingleSelect("Ja det är faktiskt 2017 i hela " + (LocalDate.now().lengthOfYear() - LocalDate.now().getDayOfYear()) + " dagar till!" + emoji_postal_horn,
                        new ArrayList<SelectItem>() {{
                            add(new SelectLink("Logga in", "message.bankid.autostart.respond", null, "bankid:///?autostarttoken={AUTOSTART_TOKEN}&redirect=expo://hedvig",  null, false));
                        }}));

        createMessage("message.bankid.autostart.respond",
                new MessageBodyBankIdCollect( "{REFERENCE_TOKEN}")
        );


        //JAG LOGGAR IN = STARTA BANKID, LOGGA IN, SEN TILLBAKS TILL message.bankidja

        // House dead-end:::

        createMessage("message.hus",
                new MessageBodySingleSelect("{HOUSE} Åh, typiskt! Just nu är det lägenheter jag kan försäkra\n\nMen jag hör gärna av mig till dig så fort jag har viktiga nyheter\n\nOch om du känner någon lägenhetsbo som du vill tipsa om mig, kan du passa på nu\n\nJag skickar ingen spam, lovar!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Skicka mig nyhetsbrev", "message.nyhetsbrev"));
                            add(new SelectOption("Jag vill tipsa någon om dig", "message.tipsa"));
                            add(new SelectOption("Tack, men nej tack", "message.avslutforstar"));

                        }}
                ));


        // All these goes to message.nagotmer
        createMessage("message.nyhetsbrev", new MessageBodyText("Härligt! Skriv in din mailadress så håller jag dig uppdaterad"));
        createMessage("message.tipsa", new MessageBodyText("Kanon! Fyll i mailadressen till den du vill att jag ska skicka ett tipsmejl till"));
        createMessage("message.frifraga", new MessageBodyText("Fråga på!\n\nSkriv vad du undrar här så hör jag och mina kollegor av oss snart " + emoji_postal_horn));

        createMessage("message.nagotmer",
                new MessageBodySingleSelect("Tack! Vill du hitta på något mer nu när vi har varandra på digitråden?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill tipsa någon om dig", "message.tipsa"));
                            add(new SelectOption("Jag har en fråga", "message.frifraga"));
                            add(new SelectOption("Nej tack!", "message.avslutok"));

                        }}
                ));

        // ----------------------------------------------- //

        createMessage("message.bankidja",
                new MessageBodySingleSelect("Tackar! Enligt infon jag har fått bor du i en lägenhet på {ADDRESS}. Stämmer det?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ja", "message.kvadrat"));
                            add(new SelectOption("Nej", "message.manuellpersonnr"));
                        }}
                ));

        createMessage("message.kvadrat", new MessageBodyNumber("Och hur många kvadrat är lägenheten?"));

        //(FUNKTION: FYLL I PERSONNR) = SCROLL KANSKE DÄR EN VÄLJER DATUM? BEHÖVS FYRA SISTA SIFFROR?

        createMessage("message.manuellpersonnr", new MessageBodyNumber("Inga problem! Då ställer jag bara några extra frågor\n\nVad är ditt personnummer?"));
        createMessage("message.varbordu", new MessageBodyText("Tack! Och var bor du någonstans?"));

        createMessage("message.student",
                new MessageBodySingleSelect("Tackar! Jag ser att du är [<27] år. Är du kanske student? " + emoji_school_satchel,
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ja", "message.studentja"));
                            add(new SelectOption("Nej", "message.lghtyp"));
                        }}
                ));

        //message.student visas endast för personer upp till 27 år. Om personen är över 27 år går de direkt vidare till message.lghtyp

        createMessage("message.studentja",
                new MessageBodySingleSelect("Se där! Då fixar jag så att du får studentrabatt",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok!", "message.lghtyp"));
                        }}
                ));

        createMessage("message.lghtyp",
                new MessageBodySingleSelect("Då fortsätter vi! Hur bor du?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Äger bostadsrätt", "message.pers"));
                            add(new SelectOption("Hyr hyresrätt", "message.pers"));
                            add(new SelectOption("Hyr i andra hand", "message.pers"));
                        }}
                ));

        // ALTERNATIVT KAN DESSA SVARSALTERNATIV GÖRAS TILL SCROLL ELLER SÅ?

        createMessage("message.pers", new MessageBodyNumber("Hoppas du trivs!\n Bor du själv eller med andra? Fyll i hur många som bor i lägenheten"));
        /*createMessage("message.pers",
                new MessageBodySingleSelect("Hoppas du trivs!\n Bor du själv eller med andra? Fyll i hur många som bor i lägenheten",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("(FUNKTION: FYLL I ANTAL PERS)", "message.sakerhet", false));
                        }}
                ));*/

//(FUNKTION: FYLL I ANTAL PERS) = SCROLL KANSKE? 1-6+ ALT. FLERVALSALTERNATIVBOXAR ELLER DEN DÄR DRA-I-SKALOR-EW-DESIGNLÖSNINGEN

        /*createMessage("message.sakerhet",
                new MessageBodySingleSelect("Tack!\nFinns någon av de här säkerhetsgrejerna i lägenheten?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("(FUNKTION: FYLL I SÄKERHETSGREJER)", "message.dyrpryl", false));
                        }}
                ));*/

		createMessage("message.sakerhet",
				new MessageBodyMultipleSelect("Tack!\nFinns någon av de här säkerhetsgrejerna i lägenheten?",
						new ArrayList<SelectItem>(){{
							add(new SelectOption("Brandvarnare", "safety.alarm"));
							add(new SelectOption("Brandsläckare", "safety.extinguisher"));
							add(new SelectOption("Säkerhetsdörr", "safety.door"));
							add(new SelectOption("Gallergrind", "safety.gate"));
							add(new SelectOption("Inbrottslarm", "safety.burglaralarm"));
						}}
				));
        //(FUNKTION: FYLL I SÄKERHETSGREJER) = SCROLL MED DE OLIKA GREJERNA KANSKE? ELLER FLERVALSALTERNATIVBOXAR?

        createMessage("message.dyrpryl",
                new MessageBodySingleSelect("Och äger du något som är värt över 75 000 kr? ",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag har inget så dyrt", "message.dyrprylnej"));
                            add(new SelectOption("Jag har dyra prylar", "message.dyrprylja"));
                        }}
                ));

        createMessage("message.dyrprylnej",
                new MessageBodySingleSelect("Okej!\nOm du skulle skaffa en dyr pryl senare är det bara att lägga till den direkt i appen så täcker jag den åt dig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok", "message.forsakringidag"));
                        }}
                ));


        createMessage("message.dyrprylja",
                new MessageBodySingleSelect("Flott!\nAlla dina prylar värda upp till 75 000 kr täcker jag automatiskt\n\nAllt värt mer än så kan du enkelt lägga till direkt i appen sen\n\nDet kostar en slant extra men oftast mindre än om du har prylen försäkrad idag",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok", "message.forsakringidag"));
                        }}
                ));

        createMessage("message.forsakringidag",
                new MessageBodySingleSelect("Då är vi snart klara!\n\nHar du någon hemförsäkring idag?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Nej", "message.forslag"));
                            add(new SelectOption("Ja", "message.forsakringidagja"));
                        }}
                ));


        /*createMessage("message.forsakringidagja",
                new MessageBodySingleSelect("Klokt av dig att ha försäkring redan!\n\nVilket försäkringsbolag har du?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("(FUNKTION: VÄLJ FÖRSÄKRINGSBOLAG)", "message.bytesinfo", false));
                        }}
                ));*/

		createMessage("message.forsakringidagja",
				new MessageBodySingleSelect("Klokt av dig att ha försäkring redan!\n\nVilket försäkringsbolag har du?",
						new ArrayList<SelectItem>(){{
							add(new SelectOption("If", "message.company.if"));
							add(new SelectOption("Trygg-Hansa", "message.company.th"));
							add(new SelectOption("Länsförsäkringar", "message.company.lf"));
						}}
				));

        //(FUNKTION: FYLL I FÖRSÄKRINGSBOLAGNAMN) = SCROLL MED DE VANLIGASTE BOLAGEN SAMT "ANNAT FÖRSÄKRINGSBOLAG"


        createMessage("message.bytesinfo",
                new MessageBodySingleSelect("Ja, ibland är det dags att prova något nytt. De kommer nog förstå\n\nOm du blir medlem hos mig sköter jag bytet åt dig\n\nSå när din gamla försäkring går ut, flyttas du automatiskt till din nya hos mig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok jag förstår", "message.forslag"));
                            add(new SelectOption("Förklara mer", "message.bytesinfo2"));
                        }}
                ));


        createMessage("message.bytesinfo2",
                new MessageBodySingleSelect("Självklart!\n\nOftast så har du ett tag kvar på bindningstiden på din gamla försäkring\n\nSå jag hör av mig till ditt försäkringsbolag med en fullmakt jag får av dig, och säger upp din gamla försäkring\n\nFullmakten skapas automatiskt när du skriver på för din nya försäkring hos mig med ditt BankID\n\nSen börjar din nya försäkring gälla direkt när bindningstiden för den gamla försäkringen gått ut\n\nDin gamla försäkring täcker dig under hela uppsägningstiden, så du behöver aldrig vara orolig att gå utan försäkring efter att du skrivit på med mig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok", "message.forslag"));
                        }}
                ));

        createMessage("message.forslag",
                new MessageBodySingleSelect("Okej! Nu har jag allt för att ge dig ditt förslag.\nSka bara räkna lite...\n\nSådärja!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Visa mig", "message.forslagpop"));
                        }}
                ));


        createMessage("message.forslagpop",
                new MessageBodySingleSelect("(FÖRSLAG VISAS I POP-UP. I POP-UP FINNS NEDAN ALTERNATIV SOM TAR EN TILLBAKA TILL CHATTEN NÄR EN VALT)",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill bli medlem", "message.medlemjabank"));
                            add(new SelectOption("Jag vill fundera", "message.fundera"));

                        }}
                ));


        createMessage("message.fundera",
                new MessageBodySingleSelect("Smart att fundera när ett viktigt val ska göras\n\nJag kanske kan ge dig mer stoff till funderande. Undrar du något av det här?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Hur vet jag att allt är tryggt?", "message.tryggt"));
                            add(new SelectOption("Vad täcks och skyddas egentligen?", "message.skydd"));
                            add(new SelectOption("Berätta mer om priset", "message.pris"));
                            add(new SelectOption("Jag vill fråga om något annat", "message.frifråga"));

                        }}
                ));

        createMessage("message.tryggt",
                new MessageBodySingleSelect("Jag har en trygghetspartner som är ett av världens största försäkringsbolag\n\nDe är där för mig så jag kan vara där för dig, oavsett. Till exempel om en storm skulle drabba hela Sverige och alla mina medlemmar skulle behöva stöd samtidigt\n\nJag är självklart också auktoriserad av Finansinspektionen " + emoji_mag,
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok! Jag vill bli medlem", "message.medlemjabank"));
                            add(new SelectOption("Jag undrar om skyddet också", "message.skydd"));
                            add(new SelectOption("Jag vill höra om priset", "message.pris"));
                            add(new SelectOption("Jag vill fråga om något annat", "message.frifråga"));

                        }}
                ));

        createMessage("message.skydd",
                new MessageBodySingleSelect("Med mig har du samma grundskydd som vanliga försäkringsbolag\n\nUtöver det ingår alltid drulle, alltså till exempel om du tappar din telefon i golvet och den går sönder, och extra reseskydd\n\nSen kan du enkelt anpassa din försäkring som du vill direkt i appen, så att du får precis det skydd du vill ha",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok! Jag vill bli medlem", "message.medlemjabank"));
                            add(new SelectOption("Berätta om tryggheten", "message.tryggt"));
                            add(new SelectOption("Hur är det med priset?", "message.pris"));
                            add(new SelectOption("Jag vill fråga om något annat", "message.frifråga"));

                        }}
                ));

        createMessage("message.pris",
                new MessageBodySingleSelect("Oftast betalar du mindre till mig än vad du skulle till andra. Och jag fokuserar alltid på att ge dig mer för pengarna\n\nGrundskyddet som jag ger är också bredare än det du oftast får på annat håll till liknande pris\n\nDet jag prioriterar allra mest är att vara där på dina villkor. Jag utvecklas alltid för att vara så snabb, smidig och smart som möjligt\n\nOch sist men inte minst! Merparten av det du betalar till mig öronmärks för dina och andra medlemmars skador. Det som blir över i medlemspotten varje år går till välgörenhet",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok! Jag vill bli medlem", "message.medlemjabank"));
                            add(new SelectOption("Berätta om tryggheten", "message.tryggt"));
                            add(new SelectOption("Jag vill veta mer om skyddet?", "message.skydd"));
                            add(new SelectOption("Jag vill fråga om något annat", "message.frifråga"));

                        }}
                ));

//DET VORE SNYGGT OM FUNDERASVARSALTERNATIVEN I medlem.fundera-trädet KUNDE FÖRSVINNA/ÄNDRAS BEROENDE PÅ VILKA EN KLICKAT PÅ! =)



        createMessage("message.medlemjabank",
                new MessageBodySingleSelect("Hurra! "+ emoji_tada +"\n\nDå behöver jag bara veta vilken bank du har innan vi skriver på så jag kan koppla upp autogiro",
                        new ArrayList<SelectItem>() {{

                            add(new SelectOption("Swedbank", "FSPA"));
                            add(new SelectOption("Forex", "FOREX"));
                            add(new SelectOption("Handelsbanken", "SHB"));
                            add(new SelectOption("Ica", "ICA"));
                            add(new SelectOption("Lansforsakringar", "LFB"));
                            add(new SelectOption("Nordea", "NB"));
                            add(new SelectOption("SBAB", "SBAB"));
                            add(new SelectOption("SEB", "SEB"));
                            add(new SelectOption("Skandia", "SKB"));
                            add(new SelectOption("Sparbanken Syd ", "SYD"));
                        }}
                ),
                (UserContext userContext, SelectItem s) -> {
                  userContext.putUserData("{BANK}", s.value);
                  userContext.putUserData("{BANK_FULL}", s.text);
                  return "message.start.account.retrieval";
                });


        createMessage("message.start.account.retrieval",
                new MessageBodySingleSelect("Då behöver vi välja det konto som pengarna ska dras ifrån. Om du har ditt BankId redor så ska jag fråga mina vänner på {BANK_FULL} om dina konotnummer.",
                        new ArrayList<SelectItem>(){{
                            add(new SelectOption("Jag är redo!", "message.fetch.accounts"));
                            add(new SelectOption("Jag är inte redo!", "message.medlemjabank"));
                        }}));

        createMessage("message.fetch.accounts",
                new MessageBodySingleSelect("Då behöver vi välja det konto som pengarna ska dras ifrån. Om du har ditt BankId redor så ska jag fråga mina vänner på {BANK_FULL} om dina konotnummer.",
                        new ArrayList<SelectItem>(){{
                            add(new SelectOption("Jag är redo!", "message.fetch.accounts"));
                            add(new SelectOption("Jag är inte redo!", "message.medlemjabank"));
                        }}),
                (userContext, item) -> {
                    this.memberService.startBankAccountRetrieval(userContext.getMemberId());
                    log.debug("HOOYWA");
                    return "";
                });

        //(FUNKTION: FYLL I BANKNAMN) = SCROLL MED DE VANLIGASTE BANKERNA SAMT "ANNAN BANK"

        createMessage("message.mail", new MessageBodyText("Tackar.\nOch din mailadress så jag kan skicka en bekräftelse när vi skrivit på?"));
        /*createMessage("message.mail",
                new MessageBodySingleSelect("Tackar.\nOch din mailadress så jag kan skicka en bekräftelse när vi skrivit på?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("(FUNKTION: FYLL I MAILADRESS)", "message.kontrakt"));

                        }}
                ));*/

        //(FUNKTION: FYLL I MAILADRESS) = FÄLT

        createMessage("message.kontrakt",
                new MessageBodySingleSelect("Tack igen.\n\nOch nu till det stora ögonblicket...\n\nHär har du allt som vi sagt samlat. Läs igenom och skriv på med ditt BankID för att godkänna din nya försäkring",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Visa kontraktet", "message.kontraktpop"));

                        }}
                ));

        createMessage("message.kontraktpop",
                new MessageBodySingleSelect("(FÖRSLAG VISAS I POP-UP. I POP-UP FINNS NEDAN ALTERNATIV SOM TAR EN TILLBAKA TILL CHATTEN NÄR EN VALT)",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill skriva på och bli Hedvig-medlem", "message.kontraktklar"));

                        }}
                ));

        createMessage("message.kontraktklar",
                new MessageBodySingleSelect(emoji_tada + " Hurra igen! "+ emoji_tada +"\n\nVälkommen, bästa nya medlem!\n\nI din inkorg finns nu en bekräftelse på allt\n\nOm du behöver eller vill något är det bara att chatta med mig i appen när som helst\n\nOch så till sist ett litet tips! Börja utforska appen genom att välja vilken välgörenhetsorganisation du vill stödja " + emoji_revlolving_hearts,
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill utforska", "message.login"));
                            add(new SelectOption("Vi hörs, Hedvig!", "message.avslutvalkommen"));

                        }}
                ));

        createMessage("message.avslutvalkommen",
                new MessageBodySingleSelect("Hej så länge och ännu en gång, varmt välkommen!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill starta om chatten (FUNKTION: OMSTART)", "onboarding.done"));

                        }}
                ));

        //(FUNKTION: OMSTART) = VORE TOPPEN MED EN FUNKTION SOM GÖR ATT FOLK KAN BÖRJA CHATTA FRÅN BÖRJAN IGEN, SÅ CHATTEN KAN BLI EN LOOP OCH GÖRAS OM IGEN OCH VISAS FÖR ANDRA PERSONER ÄN MEDLEMMEN


        createMessage("message.avslutok",
                new MessageBodySingleSelect("Okej! Trevligt att chattas, ha det fint och hoppas vi hörs igen!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill starta om chatten (FUNKTION: OMSTART)", "message.onboardingstart"));

                        }}
                ));

        //(FUNKTION: OMSTART) = VORE TOPPEN MED EN FUNKTION SOM GÖR ATT FOLK KAN BÖRJA CHATTA FRÅN BÖRJAN IGEN, SÅ CHATTEN KAN BLI EN LOOP OCH GÖRAS OM IGEN OCH VISAS FÖR ANDRA PERSONER ÄN MEDLEMMEN

        createMessage("message.cad", new MessageBodyParagraph("WIP"),"animation.bike"); // With avatar

        createMessage("message.bikedone", new MessageBodyText("Nu har du sett hur det funkar..."));

        createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
    }

    public void init() {
    	log.info("Starting onboarding conversation");
        //startConversation("message.onboardingstart"); // Id of first message
        startConversation("message.medlemjabank"); // Id of first message
    }

    // --------------------------------------------------------------------------- //

    public int getValue(MessageBodyNumber body){
    	return Integer.parseInt(body.text);
    }

    public String getValue(MessageBodySingleSelect body){

		for(SelectItem o : body.choices){
			if(SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected){
				return SelectOption.class.cast(o).value;
			}
		}
		return "";
    }

    public ArrayList<String> getValue(MessageBodyMultipleSelect body){
		ArrayList<String> selectedOptions = new ArrayList<String>();
		for(SelectItem o : body.choices){
			if(SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected){
				 selectedOptions.add(SelectOption.class.cast(o).value);
			}
		}
		return selectedOptions;
    }

    // ------------------------------------------------------------------------------- //
    @Override
    public void recieveEvent(EventTypes e, String value){

		switch(e){
		case ANIMATION_COMPLETE:
			switch(value){
				case "animation.bike":
					completeRequest("message.bikedone");
					break;
			}
		}
    }

    @Override
    public void recieveMessage(Message m) {
        log.info(m.toString());

        String nxtMsg = "";

        if(this.hasSelectItemCallback(m.id) && m.body.getClass().equals(MessageBodySingleSelect.class)) {
            MessageBodySingleSelect body = (MessageBodySingleSelect) m.body;
            nxtMsg = this.execSelectItemCallback(m.id, userContext, body.getSelectedItem());
            addToChat(m);
        }

        switch (m.id) {
	        case "message.forslagstart":
				userContext.putUserData("{HOUSE}", getValue((MessageBodySingleSelect) m.body));
	            break;
	        case "message.nyhetsbrev":
	        case "message.tipsa":
	        case "message.frifraga":
	        	userContext.putUserData("{MAIL}", m.body.text);
	        	addToChat(m);
	        	nxtMsg = "message.nagotmer";
	        	break;
	        case "message.pers":
	        	int nr_persons = getValue((MessageBodyNumber)m.body);
	        	userContext.putUserData("{NR_PERSONS}", new Integer(nr_persons).toString());
	        	if(nr_persons==1){ m.body.text = "Jag bor själv"; }
	        	else{ m.body.text = "Vi är " + nr_persons + " i hushållet"; }
	        	addToChat(m);
	        	nxtMsg = "message.sakerhet";
	        	break;
	        case "message.kvadrat":
	        	String kvm = m.body.text;
	        	userContext.putUserData("{KVM}", kvm);
                m.body.text = kvm + "kvm";
                addToChat(m);
	        	nxtMsg = "message.student";
	        	break;
	        case "message.manuellpersonnr":
	        	userContext.putUserData("{SSN}", m.body.text);
	        	addToChat(m);
	        	nxtMsg = "message.varbordu";
	        	break;
	        case "message.varbordu":
	        	userContext.putUserData("{ADDRESS}", m.body.text);
	        	addToChat(m);
	        	nxtMsg = "message.kvadrat";
	        	break;
	        case "message.mail":
	        	userContext.putUserData("{MAIL}", m.body.text);
	        	addToChat(m);
	        	nxtMsg = "message.kontrakt";
	        	break;
	        case "message.sakerhet":
	    		String safetyItems = "";
	    		MessageBodyMultipleSelect body = (MessageBodyMultipleSelect)m.body;
	    		for(SelectItem o : body.choices){
	    			if(SelectOption.class.isInstance(o)){ // Check non-link items
	    				userContext.putUserData("{"+SelectOption.class.cast(o).value+"}", SelectOption.class.cast(o).selected?"1":"0"); // Save all options selected and-non selected ones
		    			if(SelectOption.class.cast(o).selected)safetyItems += (SelectOption.class.cast(o).value + ",");
	    			}
	    		}
	        	if(safetyItems.equals(""))m.body.text = "Jag har inga sådana grejer...";
	        	else{ m.body.text = "Jag har " + safetyItems; }
	        	addToChat(m);
	        	nxtMsg = "message.dyrpryl";
	        	break;
	        case "message.forsakringidagja":
	        	String comp = getValue((MessageBodySingleSelect)m.body);
	        	userContext.putUserData("{INSURANCE_COMPANY_TODAY}", m.body.text);
	        	m.body.text = "Idag har jag " + comp;
	        	addToChat(m);
	        	nxtMsg = "message.bytesinfo";
            case "message.getname":
                String fName = m.body.text;
                userContext.putUserData("{NAME}", fName);
                m.body.text = "Jag heter " + fName;
                addToChat(m); // Response parsed to nice format
                nxtMsg = "message.greetings";

                break;

            case "message.bankid.start":
                String selectedValue = getValue((MessageBodySingleSelect)m.body);

                Optional<BankIdAuthResponse> authResponse = memberService.auth();

                nxtMsg = handleBankIdAuthRespose(nxtMsg, authResponse);

                addToChat(m);
                break;

            case "message.bankid.start.manual":
                String ssn =  m.body.text;

                Optional<BankIdAuthResponse> ssnResponse = memberService.auth(ssn);

                nxtMsg = handleBankIdAuthRespose(nxtMsg, ssnResponse);
                if(nxtMsg.equals("")) {
                    nxtMsg = "message.bankid.autostart.respond";
                }

                addToChat(m);
                break;

            case "message.bankid.autostart.send":
                //selectedValue = getSelectedSingleValue(m);
                addToChat(m);
                break;

            case "onboarding.done" :
            	userContext.onboardingComplete(true);
            	break;
            case "message.greetings":

                LocalDateTime bDate = ((MessageBodyDatePicker) m.body).date;
                log.info("Add to context:" + "{BIRTH_DATE}:" + bDate.toString());
                userContext.putUserData("{BIRTH_DATE}", bDate.toString());
                nxtMsg = "message.bye";
                //putMessage(messageList.get("message.bye"));

                break;


            default:
                break;
        }

        /*
	  * In a Single select, there is only one trigger event. Set default here to be a link to a new message
	  */
       if (nxtMsg.equals("") && m.body.getClass().equals(MessageBodySingleSelect.class)) {

           MessageBodySingleSelect body1 = (MessageBodySingleSelect) m.body;
           for (SelectItem o : body1.choices) {
               if(o.selected) {
                   m.body.text = o.text;
                   addToChat(m);
                   nxtMsg = o.value;
               }
           }
       }

       completeRequest(nxtMsg);

    }

    private String handleBankIdAuthRespose(String nxtMsg, Optional<BankIdAuthResponse> authResponse) {
        if(!authResponse.isPresent()) {
            log.error("Could not start bankIdAuthentication!");
            nxtMsg = "message.bankid.error";
        }else{
            userContext.putUserData("{AUTOSTART_TOKEN}", authResponse.get().autoStartToken);
            userContext.putUserData("{REFERENCE_TOKEN}", authResponse.get().referenceToken);
        }
        return nxtMsg;
    }

    /*
     * Generate next chat message or ends conversation
     * */
    @Override
	public void completeRequest(String nxtMsg){

		switch(nxtMsg){
			case "message.whoishedvig":
				log.info("Onboarding complete");
				userContext.onboardingComplete(true);
				break;
		    case "message.bankid.device.start":
		        //BankIdAuthResponse  authResponse = this.memberService.auth();
		        nxtMsg = "message.bankid.autostart.send";
			case "":
		        log.info("Unknown message recieved...");
		        addToChat(getMessage("error"));
			default:
				addToChat(getMessage(nxtMsg));
				break;
			}
	}

	public void bankIdAuthComplete(){
        addToChat(getMessage("message.bankidja"));
    }
}
