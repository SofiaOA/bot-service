package com.hedvig.botService.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.botService.enteties.*;

public class OnboardingConversationDevi extends Conversation {

    private static Logger log = LoggerFactory.getLogger(OnboardingConversation.class);
    private static DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public OnboardingConversationDevi(MemberChat mc, UserContext uc) {
        super("onboarding", mc, uc);
        // TODO Auto-generated constructor stub

        createMessage("message.onboardingstart",
                new MessageBodySingleSelect("Hej, jag heter Hedvig!\n\nFint att ha dig här\n\nJag är en försäkringsbot så låt mig visa vad jag gör!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ge mig ett försäkringsförslag", "message.forslagstart", false));
                            add(new SelectOption("Visa mig", "message.cad", false));
                            add(new SelectOption("Jag är redan medlem", "message.medlem", false));
                        }}
                ));

        createMessage("message.forslagstart",
                new MessageBodySingleSelect(":ok_hand:\n\nDå sätter vi igång\n\nAllt du svarar är så klart i säkert förvar hos mig :closed_lock_with_key:\n\nBor du i lägenhet eller eget hus?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Lägenhet", "message.lagenhet", false));
                            add(new SelectOption("Eget hus", "message.hus", false));
                        }}
                ));

        createMessage("message.lagenhet",
                new MessageBodySingleSelect("Toppen\n\nLogga in med ditt BankID så kan vi snabbspola fram några frågor!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag loggar in (FUNKTION: BANKID-LOGIN)", "message.bankidja", false));
                            add(new SelectOption("Jag har inget BankID", "message.manuellpersonnr", false));
                        }}
                ));

        //JAG LOGGAR IN = STARTA BANKID, LOGGA IN, SEN TILLBAKS TILL message.bankidja

        // House dead-end:::
        
        createMessage("message.hus",
                new MessageBodySingleSelect("{HOUSE} Åh, typiskt! Just nu är det lägenheter jag kan försäkra\n\nMen jag hör gärna av mig till dig så fort jag har viktiga nyheter\n\nOch om du känner någon lägenhetsbo som du vill tipsa om mig, kan du passa på nu\n\nJag skickar ingen spam, lovar!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Skicka mig nyhetsbrev", "message.nyhetsbrev", false));
                            add(new SelectOption("Jag vill tipsa någon om dig", "message.tipsa", false));
                            add(new SelectOption("Tack, men nej tack", "message.avslutforstar", false));

                        }}
                ));


        // All these goes to message.nagotmer
        createMessage("message.nyhetsbrev", new MessageBodyText("Härligt! Skriv in din mailadress så håller jag dig uppdaterad"));
        createMessage("message.tipsa", new MessageBodyText("Kanon! Fyll i mailadressen till den du vill att jag ska skicka ett tipsmejl till"));
        createMessage("message.frifraga", new MessageBodyText("Fråga på!\n\nSkriv vad du undrar här så hör jag och mina kollegor av oss snart :postal_horn:"));
        
        createMessage("message.nagotmer",
                new MessageBodySingleSelect("Tack! Vill du hitta på något mer nu när vi har varandra på digitråden?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill tipsa någon om dig", "message.tipsa", false));
                            add(new SelectOption("Jag har en fråga", "message.frifraga", false));
                            add(new SelectOption("Nej tack!", "message.avslutok", false));

                        }}
                ));

        // ----------------------------------------------- //
        
        createMessage("message.bankidja",
                new MessageBodySingleSelect("Tackar! Enligt infon jag har fått bor du i en lägenhet på (ADRESS). Stämmer det?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ja", "message.kvadrat", false));
                            add(new SelectOption("Nej", "message.manuellpersonnr", false));
                        }}
                ));

        createMessage("message.kvadrat", new MessageBodyNumber("Och hur många kvadrat är lägenheten?"));
        
        createMessage("message.manuellpersonnr",
                new MessageBodySingleSelect("Inga problem! Då ställer jag bara några extra frågor\n\nVad är ditt personnummer?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("(FUNKTION: FYLL I PERSONNR)", "message.personnr", false));
                        }}
                ));

        //(FUNKTION: FYLL I PERSONNR) = SCROLL KANSKE DÄR EN VÄLJER DATUM? BEHÖVS FYRA SISTA SIFFROR?

        createMessage("message.personnr",
                new MessageBodySingleSelect("Tack! Och var bor du någonstans?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("(FUNKTION: FYLL I ADRESS)", "message.kvadrat", false));
                        }}
                ));

        //(FUNKTION: FYLL I ADRESS) = FÄLT FÖR GATUNAMN/NR, VÅNING/LGH-NUMMER POSTADRESS?

        createMessage("message.student",
                new MessageBodySingleSelect("Tackar! Jag ser att du är [<27] år. Är du kanske student? :school_satchel:",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ja", "message.studentja", false));
                            add(new SelectOption("Nej", "message.lghtyp", false));
                        }}
                ));

        //message.student visas endast för personer upp till 27 år. Om personen är över 27 år går de direkt vidare till message.lghtyp

        createMessage("message.studentja",
                new MessageBodySingleSelect("Se där! Då fixar jag så att du får studentrabatt",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok!", "lghtyp", false));
                        }}
                ));

        createMessage("message.lghtyp",
                new MessageBodySingleSelect("Då fortsätter vi! Hur bor du?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Äger bostadsrätt", "message.pers", false));
                            add(new SelectOption("Hyr hyresrätt", "message.pers", false));
                            add(new SelectOption("Hyr i andra hand", "message.pers", false));
                        }}
                ));

        // ALTERNATIVT KAN DESSA SVARSALTERNATIV GÖRAS TILL SCROLL ELLER SÅ?

        createMessage("message.pers",
                new MessageBodySingleSelect("Hoppas du trivs!\n Bor du själv eller med andra? Fyll i hur många som bor i lägenheten",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("(FUNKTION: FYLL I ANTAL PERS)", "sakerhet", false));
                        }}
                ));

//(FUNKTION: FYLL I ANTAL PERS) = SCROLL KANSKE? 1-6+ ALT. FLERVALSALTERNATIVBOXAR ELLER DEN DÄR DRA-I-SKALOR-EW-DESIGNLÖSNINGEN

        createMessage("message.sakerhet",
                new MessageBodySingleSelect("Tack!\nFinns någon av de här säkerhetsgrejerna i lägenheten?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("(FUNKTION: FYLL I SÄKERHETSGREJER)", "dyrpryl", false));
                        }}
                ));

        //(FUNKTION: FYLL I SÄKERHETSGREJER) = SCROLL MED DE OLIKA GREJERNA KANSKE? ELLER FLERVALSALTERNATIVBOXAR?

        createMessage("message.dyrpryl",
                new MessageBodySingleSelect("Och äger du något som är värt över 75 000 kr? ",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag har inget så dyrt", "dyrprylnej", false));
                            add(new SelectOption("Jag har dyra prylar", "dyrprylja", false));
                        }}
                ));

        createMessage("message.dyrprylnej",
                new MessageBodySingleSelect("Okej!\nOm du skulle skaffa en dyr pryl senare är det bara att lägga till den direkt i appen så täcker jag den åt dig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok", "forsakringidag", false));
                        }}
                ));


        createMessage("message.dyrprylja",
                new MessageBodySingleSelect("Flott!\nAlla dina prylar värda upp till 75 000 kr täcker jag automatiskt\n\nAllt värt mer än så kan du enkelt lägga till direkt i appen sen\n\nDet kostar en slant extra men oftast mindre än om du har prylen försäkrad idag",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok", "forsakringidag", false));
                        }}
                ));

        createMessage("message.forsakringidag",
                new MessageBodySingleSelect("Då är vi snart klara!\n\nHar du någon hemförsäkring idag?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Nej", "forslag", false));
                            add(new SelectOption("Ja", "forsakringidagja", false));
                        }}
                ));


        createMessage("message.forsakringidagja",
                new MessageBodySingleSelect("Klokt av dig att ha försäkring redan!\n\nVilket försäkringsbolag har du?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("(FUNKTION: VÄLJ FÖRSÄKRINGSBOLAG)", "bytesinfo", false));
                        }}
                ));


        //(FUNKTION: FYLL I FÖRSÄKRINGSBOLAGNAMN) = SCROLL MED DE VANLIGASTE BOLAGEN SAMT "ANNAT FÖRSÄKRINGSBOLAG"


        createMessage("message.bytesinfo",
                new MessageBodySingleSelect("Ja, ibland är det dags att prova något nytt. De kommer nog förstå\n\nOm du blir medlem hos mig sköter jag bytet åt dig\n\nSå när din gamla försäkring går ut, flyttas du automatiskt till din nya hos mig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok jag förstår", "forslag", false));
                            add(new SelectOption("Förklara mer", "bytesinfo2", false));
                        }}
                ));


        createMessage("message.bytesinfo2",
                new MessageBodySingleSelect("Självklart!\n\nOftast så har du ett tag kvar på bindningstiden på din gamla försäkring\n\nSå jag hör av mig till ditt försäkringsbolag med en fullmakt jag får av dig, och säger upp din gamla försäkring\n\nFullmakten skapas automatiskt när du skriver på för din nya försäkring hos mig med ditt BankID\n\nSen börjar din nya försäkring gälla direkt när bindningstiden för den gamla försäkringen gått ut\n\nDin gamla försäkring täcker dig under hela uppsägningstiden, så du behöver aldrig vara orolig att gå utan försäkring efter att du skrivit på med mig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok", "forslag", false));
                        }}
                ));

        createMessage("message.forslag",
                new MessageBodySingleSelect("Okej! Nu har jag allt för att ge dig ditt förslag.\nSka bara räkna lite...\n\nSådärja!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Visa mig", "forslagpop", false));
                        }}
                ));


        createMessage("message.forslagpop",
                new MessageBodySingleSelect("(FÖRSLAG VISAS I POP-UP. I POP-UP FINNS NEDAN ALTERNATIV SOM TAR EN TILLBAKA TILL CHATTEN NÄR EN VALT)",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill bli medlem", "medlemjabank", false));
                            add(new SelectOption("Jag vill fundera", "fundera", false));

                        }}
                ));


        createMessage("message.fundera",
                new MessageBodySingleSelect("Smart att fundera när ett viktigt val ska göras\n\nJag kanske kan ge dig mer stoff till funderande. Undrar du något av det här?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Hur vet jag att allt är tryggt?", "tryggt", false));
                            add(new SelectOption("Vad täcks och skyddas egentligen?", "skydd", false));
                            add(new SelectOption("Berätta mer om priset", "pris", false));
                            add(new SelectOption("Jag vill fråga om något annat", "frifråga", false));

                        }}
                ));

        createMessage("message.tryggt",
                new MessageBodySingleSelect("Jag har en trygghetspartner som är ett av världens största försäkringsbolag\n\nDe är där för mig så jag kan vara där för dig, oavsett. Till exempel om en storm skulle drabba hela Sverige och alla mina medlemmar skulle behöva stöd samtidigt\n\nJag är självklart också auktoriserad av Finansinspektionen :mag:",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok! Jag vill bli medlem", "medlemjabank", false));
                            add(new SelectOption("Jag undrar om skyddet också", "skydd", false));
                            add(new SelectOption("Jag vill höra om priset", "pris", false));
                            add(new SelectOption("Jag vill fråga om något annat", "frifråga", false));

                        }}
                ));

        createMessage("message.skydd",
                new MessageBodySingleSelect("Med mig har du samma grundskydd som vanliga försäkringsbolag\n\nUtöver det ingår alltid drulle, alltså till exempel om du tappar din telefon i golvet och den går sönder, och extra reseskydd\n\nSen kan du enkelt anpassa din försäkring som du vill direkt i appen, så att du får precis det skydd du vill ha",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok! Jag vill bli medlem", "medlemjabank", false));
                            add(new SelectOption("Berätta om tryggheten", "tryggt", false));
                            add(new SelectOption("Hur är det med priset?", "pris", false));
                            add(new SelectOption("Jag vill fråga om något annat", "frifråga", false));

                        }}
                ));

        createMessage("message.pris",
                new MessageBodySingleSelect("Oftast betalar du mindre till mig än vad du skulle till andra. Och jag fokuserar alltid på att ge dig mer för pengarna\n\nGrundskyddet som jag ger är också bredare än det du oftast får på annat håll till liknande pris\n\nDet jag prioriterar allra mest är att vara där på dina villkor. Jag utvecklas alltid för att vara så snabb, smidig och smart som möjligt\n\nOch sist men inte minst! Merparten av det du betalar till mig öronmärks för dina och andra medlemmars skador. Det som blir över i medlemspotten varje år går till välgörenhet",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok! Jag vill bli medlem", "medlemjabank", false));
                            add(new SelectOption("Berätta om tryggheten", "tryggt", false));
                            add(new SelectOption("Jag vill veta mer om skyddet?", "skydd", false));
                            add(new SelectOption("Jag vill fråga om något annat", "frifråga", false));

                        }}
                ));

//DET VORE SNYGGT OM FUNDERASVARSALTERNATIVEN I medlem.fundera-trädet KUNDE FÖRSVINNA/ÄNDRAS BEROENDE PÅ VILKA EN KLICKAT PÅ! =)



        createMessage("message.medlemjabank",
                new MessageBodySingleSelect("Hurra! :tada:\n\nDå behöver jag bara veta vilken bank du har innan vi skriver på så jag kan koppla upp autogiro",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("(FUNKTION: FYLL I BANKNAMN)", "mail", false));

                        }}
                ));

        //(FUNKTION: FYLL I BANKNAMN) = SCROLL MED DE VANLIGASTE BANKERNA SAMT "ANNAN BANK"

        createMessage("message.mail",
                new MessageBodySingleSelect("Tackar.\nOch din mailadress så jag kan skicka en bekräftelse när vi skrivit på?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("(FUNKTION: FYLL I MAILADRESS)", "kontrakt", false));

                        }}
                ));

        //(FUNKTION: FYLL I MAILADRESS) = FÄLT

        createMessage("message.kontrakt",
                new MessageBodySingleSelect("Tack igen.\n\nOch nu till det stora ögonblicket...\n\nHär har du allt som vi sagt samlat. Läs igenom och skriv på med ditt BankID för att godkänna din nya försäkring",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Visa kontraktet", "kontraktpop", false));

                        }}
                ));

        createMessage("message.kontraktpop",
                new MessageBodySingleSelect("(FÖRSLAG VISAS I POP-UP. I POP-UP FINNS NEDAN ALTERNATIV SOM TAR EN TILLBAKA TILL CHATTEN NÄR EN VALT)",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill skriva på och bli Hedvig-medlem", "kontraktklar", false));

                        }}
                ));

        createMessage("message.kontraktklar",
                new MessageBodySingleSelect(":tada: Hurra igen! :tada:\n\nVälkommen, bästa nya medlem!\n\nI din inkorg finns nu en bekräftelse på allt\n\nOm du behöver eller vill något är det bara att chatta med mig i appen när som helst\n\nOch så till sist ett litet tips! Börja utforska appen genom att välja vilken välgörenhetsorganisation du vill stödja :revolving_hearts:",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill utforska", "login", false));
                            add(new SelectOption("Vi hörs, Hedvig!", "avslutvalkommen", false));

                        }}
                ));

        createMessage("message.avslutvalkommen",
                new MessageBodySingleSelect("Hej så länge och ännu en gång, varmt välkommen!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill starta om chatten (FUNKTION: OMSTART)", "message.onboardingstart", false));

                        }}
                ));

        //(FUNKTION: OMSTART) = VORE TOPPEN MED EN FUNKTION SOM GÖR ATT FOLK KAN BÖRJA CHATTA FRÅN BÖRJAN IGEN, SÅ CHATTEN KAN BLI EN LOOP OCH GÖRAS OM IGEN OCH VISAS FÖR ANDRA PERSONER ÄN MEDLEMMEN


        createMessage("message.avslutok",
                new MessageBodySingleSelect("Okej! Trevligt att chattas, ha det fint och hoppas vi hörs igen!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill starta om chatten (FUNKTION: OMSTART)", "message.onboardingstart", false));

                        }}
                ));

        //(FUNKTION: OMSTART) = VORE TOPPEN MED EN FUNKTION SOM GÖR ATT FOLK KAN BÖRJA CHATTA FRÅN BÖRJAN IGEN, SÅ CHATTEN KAN BLI EN LOOP OCH GÖRAS OM IGEN OCH VISAS FÖR ANDRA PERSONER ÄN MEDLEMMEN





        //SLUT PÅ DEVIS FIX Å TRIX



        createMessage("message.cad", new MessageBodyText("WIP"));

        createMessage("message.medlem", new MessageBodyText("Välkommen tillbaka :hugging:\n\nLogga in med ditt BankID-inlogg, så är du inne på ditt konto igen"));

        createMessage("message.greetings", new MessageBodyDatePicker("Hej {NAME}, kul att du gillar försäkring :). När är du född?", LocalDateTime.parse("1986-04-08 00:00", datetimeformatter)));

        createMessage("message.bye", new MessageBodySingleSelect("Ok {NAME}, så det jag vet om dig är att du är förr {BIRTH_DATE}, jag hör av mig!",
                new ArrayList<SelectItem>() {{
                    add(new SelectLink("Starta bank id", "AssetTracker", "bankid://", "http://hedvig.com"));
                    add(new SelectOption("Ladda upp foto", "message.photo_upload", false));
                    add(new SelectOption("Spela in video", "message.video", false));
                    add(new SelectOption("You need a hero!", "message.hero", false));
                }}
        ));

        createMessage("message.photo_upload", new MessageBodyPhotoUpload("Här kan du ladda upp en bild..", "https://gateway.hedvig.com/asset/fileupload/"));

        createMessage("message.video", new MessageBodyAudio("Här kan du spela in en video om vad som hänt...", "http://videoploadurl"));

        createMessage("message.hero", new MessageBodyHero("You need a hero!", "http://www.comedyflavors.com/wp-content/uploads/2015/02/hero.gif"));


        createMessage("message.changecompany",
                new MessageBodyMultipleSelect("Ok, vilket bolag har du idag?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("If", "message.company.if", false));
                            add(new SelectOption("TH", "message.company.th", false));
                            add(new SelectOption("LF", "message.company.lf", false));
                        }}
                ));

        createMessage("message.whyinsurance", new MessageBodyText("Hemförsäkring behöver alla!"));

        createMessage("message.whoishedvig", new MessageBodyText("En försäkringsbot!"));
        createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
    }

    public void init() {
        startConversation("message.onboardingstart"); // Id of first message
    }

    public String getSelectedSingleValue(Message m){
		MessageBodySingleSelect body = (MessageBodySingleSelect)m.body;
		
		for(SelectItem o : body.choices){
			if(SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected){
				return SelectOption.class.cast(o).value;
			}
		}   	
		return "";
    }
    
    public ArrayList<String> getSelectedMultipleValue(Message m){
		MessageBodySingleSelect body = (MessageBodySingleSelect)m.body;
		ArrayList<String> selectedOptions = new ArrayList<String>();
		for(SelectItem o : body.choices){
			if(SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected){
				 selectedOptions.add(SelectOption.class.cast(o).value);
			}
		}   
		return selectedOptions;
    }
    
    @Override
    public void recieveMessage(Message m) {
        log.info(m.toString());

        String nxtMsg = "";

        
        switch (m.id) {
	        case "message.forslagstart":
				userContext.putUserData("{HOUSE}", getSelectedSingleValue(m));
	            break;   
	        case "message.nyhetsbrev":
	        case "message.tipsa":
	        case "message.frifraga":
	        	nxtMsg = "message.nagotmer";
	        	break;
	        case "message.kvadrat":
	        	userContext.putUserData("{KVM}", getSelectedSingleValue(m));
	        	nxtMsg = "message.student";
	        	break;	        	
            case "message.getname":

                String fName = m.body.text;
                userContext.putUserData("{NAME}", fName);
                m.body.text = "Jag heter " + fName;
                putMessage(m); // Response parsed to nice format
                nxtMsg = "message.greetings";
                //putMessage(messageList.get("message.greetings"));

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
       if (m.body.getClass().equals(MessageBodySingleSelect.class)) {

           MessageBodySingleSelect body1 = (MessageBodySingleSelect) m.body;
           for (SelectItem o : body1.choices) {
               if (SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected) {
                   m.body.text = SelectOption.class.cast(o).text;
                   putMessage(m);
                   nxtMsg = SelectOption.class.cast(o).value;
               }
           }
       } 
       
		// Check which next message is an act accordingly
		switch(nxtMsg){
			case "message.whoishedvig": 
				log.info("Onboarding complete");
				userContext.onboardingComplete(true);
				break;
			case "":
		        log.info("Unknown message recieved...");
		        putMessage(messageList.get("error"));				
			default:
				putMessage(messageList.get(nxtMsg));
				break;
		}

    }

}
