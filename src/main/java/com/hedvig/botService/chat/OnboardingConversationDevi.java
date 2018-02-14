package com.hedvig.botService.chat;

import com.hedvig.botService.dataTypes.*;
import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.SignupCode;
import com.hedvig.botService.enteties.SignupCodeRepository;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdSignResponse;
import com.hedvig.botService.serviceIntegration.memberService.exceptions.ErrorType;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.events.SignedOnWaitlistEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Component
public class OnboardingConversationDevi extends Conversation implements BankIdChat {

    public static final String LOGIN = "{LOGIN}";
    /*
             * Need to be stateless. I.e no data beyond response scope
             * 
             * Also, message names cannot end with numbers!! Numbers are used for internal sectioning
             * */
    private static Logger log = LoggerFactory.getLogger(OnboardingConversationDevi.class);
    private static DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final ApplicationEventPublisher publisher;
    private final MemberService memberService;
    private final ProductPricingService productPricingService;

    public static enum ProductTypes {BRF, RENT, RENT_BRF, SUBLET_RENTAL, SUBLET_BRF, STUDENT, LODGER};
    //private final MemberService memberService;
    //private final ProductPricingService productPricingClient;


    public final static String emoji_smile = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x81}, Charset.forName("UTF-8"));
    public final static String emoji_hand_ok = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x91, (byte)0x8C}, Charset.forName("UTF-8"));
    public final static String emoji_closed_lock_with_key = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x94, (byte)0x90}, Charset.forName("UTF-8"));
    public final static String emoji_postal_horn = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x93, (byte)0xAF}, Charset.forName("UTF-8"));
    public final static String emoji_school_satchel = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x8E, (byte)0x92}, Charset.forName("UTF-8"));
    public final static String emoji_mag = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x94, (byte)0x8D}, Charset.forName("UTF-8"));
    public final static String emoji_revlolving_hearts = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x92, (byte)0x9E}, Charset.forName("UTF-8"));
    public final static String emoji_tada = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x8E, (byte)0x89}, Charset.forName("UTF-8"));
    public final static String emoji_thumbs_up = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x91, (byte)0x8D}, Charset.forName("UTF-8"));
    public final static String emoji_hug = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0xA4, (byte)0x97}, Charset.forName("UTF-8"));
    public final static String emoji_waving_hand = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x91, (byte)0x8B}, Charset.forName("UTF-8"));
    public final static String emoji_flushed_face = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0xB3}, Charset.forName("UTF-8"));
    public final static String emoji_thinking = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0xA4, (byte)0x94}, Charset.forName("UTF-8"));

    private final SignupCodeRepository signupRepo;
    private final ConversationFactory conversationFactory;

    //@Value("${hedvig.gateway.url:http://gateway.hedvig.com}")
    public String gatewayUrl = "http://gateway.hedvig.com";
    
    @Autowired
    public OnboardingConversationDevi(
            MemberService memberService,
            ProductPricingService productPricingClient,
            SignupCodeRepository signupRepo,
            ApplicationEventPublisher publisher,
            ConversationFactory conversationFactory) {
        super("onboarding");
        this.memberService = memberService;
        this.productPricingService = productPricingClient;
        this.signupRepo = signupRepo;
        this.publisher = publisher;
        this.conversationFactory = conversationFactory;


        Image hImage = new Image("https://s3.eu-central-1.amazonaws.com/com-hedvig-web-content/Hedvig_Icon-60%402x.png",120,120);
        
        createChatMessage("message.onboardingstart",
                new MessageBodySingleSelect("Hej! Det är jag som är Hedvig " + emoji_waving_hand 
                		+"\fKul att ha dig här!"
                		+"\fIngenting är viktigare för mig än att du ska få fantastisk service"
                		+"\fMen eftersom många vill bli medlemmar just nu, så måste jag ta in ett begränsat antal i taget",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Sätt upp mig på väntelistan", "message.waitlist"));
                            add(new SelectOption("Jag har fått en aktiveringskod", "message.activate"));
                            add(new SelectOption("Kolla min plats på väntelistan", "message.signup.checkposition"));
                        }}
                ));

        createChatMessage("message.membernotfound",
                new MessageBodySingleSelect("Hmm, det verkar som att du inte är medlem här hos mig ännu"
                        +"\fJag vill gärna ha dig som medlem och ingenting är viktigare för mig än att du ska få fantastisk service"
                        +"\fMen eftersom att det är många som vill bli medlemmar just nu, så måste jag ta in ett begränsat antal i taget",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Sätt upp mig på väntelistan", "message.waitlist"));
                            add(new SelectOption("Jag har en aktiveringskod", "message.activate"));
                            add(new SelectOption("Kolla min plats på väntelistan", "message.signup.checkposition"));
                        }}
                ));

        
        createMessage("message.waitlist", new MessageBodyText("Det ordnar jag! Vad är din mailadress? "));
        setExpectedReturnType("message.waitlist", new EmailAdress());
        
        createMessage("message.signup.email", new MessageBodyText("Det ordnar jag! Vad är din mailadress? "));
        setExpectedReturnType("message.signup.email", new EmailAdress());
        
        /*createChatMessage("message.signup.checkposition",
                new MessageBodyParagraph("Du står på plats {SIGNUP_POSITION} på väntelistan"
                		+"\fJag ska göra mitt bästa för att du ska kunna bli medlem så snart som möjligt!"
                		//+"\fJag skickar en länk till din mail där du kan se din köplats"
                		+"\fHa det fint så länge!"
                ));
        addRelay("message.signup.checkposition", "message.signup.flerval");*/
        
        createChatMessage("message.signup.checkposition",
	        new MessageBodySingleSelect("Du står på plats {SIGNUP_POSITION} på väntelistan"
	                		+"\fSå snart jag har gett alla framför dig en chans att bli medlem så är det din tur!"
	                		+"\fKom tillbaka hit om du vill kolla din plats, eller för att ange aktiveringskoden när du får den på mailen"
	                		+"\fHa en fin dag, så hörs vi snart!",
	                        new ArrayList<SelectItem>() {{
	                            add(new SelectOption("Kolla min plats på listan", "message.signup.checkposition"));
	                            add(new SelectOption("Jag har fått en aktiveringskod", "message.activate"));
	                        }}
	        ));
        
        /*createChatMessage("message.waitlist.tack",
                new MessageBodySingleSelect("Tack! Du står på plats {SIGNUP_POSITION} på väntelistan"
                		+"\fJag ska göra mitt bästa för att du ska kunna bli medlem så snart som möjligt!"
                		//+"\fJag skickar en länk till din mail där du kan se din köplats"
                		+"\fHa det fint så länge!",
                        new ArrayList<SelectItem>() {{
                            //add(new SelectOption("Jag vill starta om chatten", "message.onboardingstart"));
                            //add(new SelectLink("Det samma " + emoji_waving_hand,null,null,null,"http://www.hedvig.com/",false)); // TODO: fixa denna för web
                        }}
                ));*/   
        
        /*createMessage("message.waitlist.user.alreadyactive",
                new MessageBodyText("Du borde redan ha fått en aktiveringskod. Kolla din mailkorg och skriv sedan in koden här"
                ));*/
        
        createChatMessage("message.waitlist.user.alreadyactive",
                new MessageBodyText("Grattis! " + emoji_tada + " Nu kan du bli medlem hos Hedvig\fKolla din mail, där ska du ha fått en aktiveringkod som du ska ange här\fVi ses snart! " + emoji_smile
                ));
        
        createChatMessage("message.activate.code.used",
        new MessageBodySingleSelect("Det verkar som koden redan är använd... \fHar du aktiverat koden på en annan enhet så kan du logga in direkt med bankId.",
                new ArrayList<SelectItem>() {{
                    add(new SelectOption("Jag är redan medlem och vill logga in", "message.medlem"));
                }}
        ));
        
        createMessage("message.signup.flerval",
                new MessageBodySingleSelect("",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Kolla min plats på väntelistan", "message.signup.checkposition"));
                            add(new SelectOption("Jag har fått en aktiveringskod", "message.activate"));
                        }}
                ));        

        createMessage("message.activate.notactive",
    	        new MessageBodySingleSelect("Du verkar redan stå på väntelistan. Din plats är {SIGNUP_POSITION}!",
    	                        new ArrayList<SelectItem>() {{
    	                            add(new SelectOption("Kolla min plats på väntelistan", "message.signup.checkposition"));
    	                            add(new SelectOption("Jag har fått en aktiveringskod", "message.activate"));
    	                        }}
    	        ));
        /*createChatMessage("message.activate.notactive",
                new MessageBodyParagraph("Du verkar redan stå på väntelistan. Din plats är {SIGNUP_POSITION}"
                ));
        addRelay("message.activate.notactive", "message.signup.flerval");   */     
        
        // TODO: chatMessages have a bug: they need to be multiline... pls fix
        createMessage("message.activate.nocode",
    	        new MessageBodySingleSelect("Jag känner inte igen den koden tyvärr " + emoji_thinking,
    	                        new ArrayList<SelectItem>() {{
    	                            add(new SelectOption("Kolla min plats på väntelistan", "message.signup.checkposition"));
    	                            add(new SelectOption("Jag har fått en aktiveringskod", "message.activate"));
    	                        }}
    	        ));
        
        /*createMessage("message.activate.nocode", new MessageBodyParagraph("Jag känner inte igen den koden tyvärr " + emoji_thinking),2000);
        //createMessage("message.activate.nocode.tryagain", new MessageBodyText("Pröva att ange koden igen"));
        addRelay("message.activate.nocode","message.signup.flerval");*/

        
        createMessage("message.activate", new MessageBodyText("Kul! Skriv in din kod här"));
        
        createMessage("message.activate.ok.a", new MessageBodyParagraph("Välkommen!"),1000);
        addRelay("message.activate.ok.a","message.activate.ok.b");
        
        createMessage("message.activate.ok.b", new MessageBodyParagraph("Nu ska jag ta fram ett försäkringsförslag åt dig"),2000);
        addRelay("message.activate.ok.b","message.forslagstart");
        
        
        createMessage("message.uwlimit.tack",
                new MessageBodySingleSelect("Tack! Jag hör av mig så fort jag kan",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill starta om chatten", "message.onboardingstart"));

                        }}
                ));
        

        // --------------- OLD --------------------------- //
        /*createChatMessage("message.onboardingstart",
                new MessageBodySingleSelect("Hej, jag heter Hedvig! " + emoji_waving_hand +"\fJag tar fram ett försäkringsförslag till dig på nolltid",
                        new ArrayList<SelectItem>() {{
                            //add(new SelectOption("Berätta!", "message.cad"));
                            add(new SelectOption("Låter bra!", "message.forslagstart"));
                            add(new SelectOption("Jag är redan medlem", "message.bankid.start"));
                            //add(new SelectOption("Skapa en medlem åt mig", "message.kontraktklar4"));
                            //add(new SelectOption("[Debug:audio test]", "message.audiotest"));
                            //add(new SelectOption("[Debug:photo test]", "message.phototest"));
                        }}
                ));*/
        
        createMessage("message.audiotest", new MessageBodyAudio("Här kan du testa audio", "/claims/fileupload"),2000);
        createMessage("message.phototest", new MessageBodyPhotoUpload("Här kan du testa fotouppladdaren", "/asset/fileupload"),2000);
        createMessage("message.fileupload.result",
                new MessageBodySingleSelect("Ok uppladdningen gick bra!",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectOption("Hem", "message.onboardingstart"));
                        }}
                ));
        //createMessage("message.mail", new MessageBodyText("Vad är din email?"), "h_symbol",2000);
        
        createChatMessage("message.cad",
                new MessageBodySingleSelect("Så här, egentligen är försäkring en riktigt bra grej\f"
                		+ "Därför har jag ändrat på hur försäkring funkar, för det ska vara lätt när det är svårt\f"
                		+ "När något går snett, får du hjälp\f"
                		+ "Men många tycker det är krångligt\f"
                		+ "Därför har jag ändrat på hur försäkring funkar, för det ska vara lätt när det är svårt",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Hur då?", "message.tellme"));
                        }}
                ));

        createChatMessage("message.tellme",
                new MessageBodySingleSelect("Jo, vanligtvis tjänar försäkringsbolag mer ju mindre de betalar ut\f"
                		+ "Det gör inte jag\f"
                		+ "Jag lägger istället pengarna i en medlemspott som endast går till skador\f"
                		+ "Och fokuserar på att betala ut snabbt\f"
                		+ "Finns några pengar kvar efter att skadorna är betalda ges det till en välgörenhetsorganisation",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Men hur tjänar du pengar då?", "message.tellme10"));
                        }}
                )); 

        createMessage("message.tellme10",
                new MessageBodySingleSelect("Jag tar en fast avgift för att hålla igång allt. Den avgiften är helt separerad från medlemspotten",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectOption("Ok, ge mig ett försäkringsförslag", "message.forslagstart"));
                            add(new SelectOption("Vad är poängen med att göra saker så här?", "message.tellme11"));
                        }}
                ));             
        
        createChatMessage("message.tellme11",
                new MessageBodySingleSelect("Att du ska vara säker på att jag alltid är schysst mot dig!\f"
                		+ "Eftersom jag bara tar en fast avgift vet du att jag inte kommer försöka krångla med dig för att tjäna mer pengar\f"
                		+ "Så mitt enda mål är att göra saker supersmidigt för dig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Hm, hur smidigt är supersmidigt?", "message.tellme14"));
                        }}
                ));     

        createChatMessage("message.tellme14",
                new MessageBodySingleSelect("Du kan göra det mesta direkt digitalt genom att chatta med mig\f"
                		+ "Från att skaffa en skräddarsydd hemförsäkring\f"
                		+ "Till att rapportera om något hänt och få ersättning\f"
                		+ "Jag är ju en bot, så jag finns i din mobil eller dator 24/7",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectOption("Kör på, skräddarsy mitt försäkringsförslag", "message.forslagstart"));
                            add(new SelectOption("Men är det här tryggt?", "message.tellme19"));
                        }}
                )); 

        createChatMessage("message.tellme19",
                new MessageBodySingleSelect("Jag samarbetar med ett av de största försäkringsbolagen i världen\f"
                		+ "De tryggar mig, så att jag kan betala ut ersättning till dig vad som än händer\f"
                		+ "Men nog om mig nu! Dags att lära känna dig",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectOption("Helt rätt prio! Ge mig ett försäkringsförslag", "message.forslagstart"));
                        }}
                ));   

        createMessage("message.mockme", new MessageBodyText("Ok! Klart"+ emoji_hand_ok + " du heter nu {NAME} och bor på {ADDRESS} i en {HOUSE}.\n\f Vilket meddelande vill du gå till?"));

        createMessage("message.medlem",
                new MessageBodySingleSelect("Välkommen tillbaka "+ emoji_hug +"\n\n Logga in med BankID så är du inne i appen igen",
                        new ArrayList<SelectItem>() {{
                            add(new SelectLink("Logga in", "message.bankid.autostart.respond", null, "bankid:///?autostarttoken={AUTOSTART_TOKEN}&redirect={LINK_URI}",  null, false));

                           // add(new SelectOption("Logga in", "message.bankidja"));
                        }}
                ),
                (m, uc) -> {
                    UserData obd = uc.getOnBoardingData();
                    if(m.getSelectedItem().value.equals("message.bankid.autostart.respond"))
                    {
                    	uc.putUserData(LOGIN, "true");
                        obd.setBankIdMessage("message.medlem");
                    }

                    return "";
                }
       );
        setupBankidErrorHandlers("message.medlem");

        createMessage("message.forslagstart",
                new MessageBodySingleSelect(
                		"Första frågan! Bor du i lägenhet eller eget hus?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Lägenhet", "message.lagenhet"));
                            add(new SelectOption("Hus", "message.hus"));
                        }}
                ), "h_to_house");


        createMessage("message.lagenhet",
                new MessageBodySingleSelect("Toppen! Har du BankID? I så fall kan vi hoppa över några frågor!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectLink("Logga in med BankID", "message.bankid.autostart.respond", null, "bankid:///?autostarttoken={AUTOSTART_TOKEN}&redirect={LINK_URI}",  null, false));
                            add(new SelectOption("Jag har inte BankID", "message.manuellnamn"));
                        }}
                ),
                (m, uc) -> {
                    UserData obd = uc.getOnBoardingData();
                    if(m.getSelectedItem().value.equals("message.bankid.autostart.respond"))
                    {
                        obd.setBankIdMessage("message.lagenhet");
                    }

                    return "";
                }
        );


        setupBankidErrorHandlers("message.lagenhet");

        createMessage("message.missing.bisnode.data",
                new MessageBodyParagraph("Jag hittade tyvärr inte dina uppgifter. Men...")
        );
        addRelay("message.missing.bisnode.data","message.manuellnamn");
        
        
        createMessage("message.start.login",
                new MessageBodyParagraph("Välkommen tillbaka! " + emoji_hug), 1500);
        addRelay("message.start.login","message.bankid.start");
        
        createMessage("message.bankid.start",
                new MessageBodySingleSelect("Bara att logga in så ser du din försäkring",
                        new ArrayList<SelectItem>() {{
                            add(new SelectLink("Logga in med BankID", "message.bankid.autostart.respond", null, "bankid:///?autostarttoken={AUTOSTART_TOKEN}&redirect={LINK_URI}",  null, false));
                        }}
                ),
                (m, uc) -> {
                    UserData obd = uc.getOnBoardingData();
                    if(m.getSelectedItem().value.equals("message.bankid.autostart.respond"))
                    {
                        obd.setBankIdMessage("message.bankid.start");
                    }

                    return "";
                }
        );

        setupBankidErrorHandlers("message.bankid.start");
        
        
        createMessage("message.bankid.start.manual",
                new MessageBodyNumber("Om du anger ditt personnumer så får du använda bankId på din andra enhet" + emoji_smile
                ));


        createMessage("message.bankid.error",
                new MessageBodyParagraph("Hmm, något blev fel vi försöker igen" + emoji_flushed_face), 1500);

        createMessage("message.bankid.start.manual.error",
                new MessageBodyParagraph("Hmm nu blev något fel! Vi försöker igen \"" + emoji_flushed_face));
        addRelay("message.bankid.start.manual.error", "message.bankid.start.manual");

        createMessage("message.bankid.autostart.respond",
                new MessageBodyBankIdCollect( "{REFERENCE_TOKEN}")
        );

        createChatMessage("message.hus",
                new MessageBodySingleSelect("Åh, typiskt! Just nu är det bara lägenheter jag kan försäkra\f"
                		+ "Men jag hör gärna av mig till dig så fort jag är viktiga nyheter om annat jag kan försäkra\f"
                		+ "Jag skickar ingen spam. Lovar!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Skicka mig nyhetsbrev", "message.nyhetsbrev"));
                            add(new SelectOption("Tack, men nej tack", "message.avslutok"));

                        }}
                ));


        // All these goes to message.nagotmer
        createMessage("message.nyhetsbrev", new MessageBodyText("Vad är din mailadress?"));
        setExpectedReturnType("message.nyhetsbrev", new EmailAdress());
        createMessage("message.tipsa", new MessageBodyText("Kanon! Fyll i mailadressen till den du vill att jag ska skicka ett tipsmail till"));
        setExpectedReturnType("message.tipsa", new EmailAdress());
        createMessage("message.frifraga", new MessageBodyText("Fråga på! Skriv vad du undrar här så hör jag och mina kollegor av oss snart " + emoji_postal_horn));

        
        
        createMessage("message.nagotmer",
                new MessageBodySingleSelect("Tack! Vill du hitta på något mer nu när vi har varandra på tråden?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill tipsa någon om dig", "message.tipsa"));
                            add(new SelectOption("Jag har en fråga", "message.frifraga"));
                            add(new SelectOption("Nej tack!", "message.avslutok"));

                        }}
                ));

        createMessage("message.bankidja",
                new MessageBodySingleSelect("Tack {NAME}! Stämmer det att du bor på {ADDRESS}?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ja", "message.kvadrat"));
                            add(new SelectOption("Nej", "message.varbordufeladress"));
                        }}
                ));

        createMessage("message.bankidja.noaddress",
                new MessageBodyText("Tack {NAME}! Nu skulle jag behöva veta vilken gatuadress bor du på?")
                );

        createMessage("message.varbordufeladress", new MessageBodyText("Inga problem! Vilken gatuadress bor du på?"));
        createMessage("message.varbordufelpostnr", new MessageBodyNumber("Och vad har du för postnummer?"));
        setExpectedReturnType("message.varbordufelpostnr", new ZipCodeSweden());

        createMessage("message.kvadrat", new MessageBodyNumber("Hur många kvadratmeter är lägenheten?"));
        setExpectedReturnType("message.kvadrat", new LivingSpaceSquareMeters());

        createChatMessage("message.manuellnamn", new MessageBodyText("Inga problem! Då ställer jag bara några extra frågor nu\fMen om du vill bli medlem sen så måste du signera med BankID, bara så du vet!\fVad heter du i förnamn?"));
        
        createMessage("message.manuellfamilyname", new MessageBodyText("Kul att ha dig här {NAME}! " + emoji_hug + " Vad heter du i efternamn?"));
        
        createMessage("message.manuellpersonnr", new MessageBodyNumber("Tack! Vad är ditt personnummer? (12 siffror)"));
        setExpectedReturnType("message.manuellpersonnr", new SSNSweden());
        createMessage("message.varborduadress", new MessageBodyText("Tack! Och vilken gatuadress bor du på?"));
        createMessage("message.varbordupostnr", new MessageBodyNumber("Vad är ditt postnummer?"));
        setExpectedReturnType("message.varbordupostnr", new ZipCodeSweden());
        
        createMessage("message.student",
                new MessageBodySingleSelect("Tackar! Jag ser att du är under 27. Är du kanske student? " + emoji_school_satchel,
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ja", "message.studentja"));
                            add(new SelectOption("Nej", "message.lghtyp"));
                        }}
                ));

        createMessage("message.studentja",
                new MessageBodySingleSelect("Se där! Då fixar jag så att du får studentrabatt",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ok!", "message.lghtyp"));
                        }}
                ));

        createMessage("message.lghtyp",
                new MessageBodySingleSelect("Okej! Hur bor du?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Äger bostadsrätt", ProductTypes.BRF.toString()));
                            add(new SelectOption("Hyr hyresrätt", ProductTypes.RENT.toString()));
                            //add(new SelectOption("Hyr bostadsrätt", ProductTypes.SUBLET.toString()));
                            add(new SelectOption("Hyr i andra hand", "message.lghtyp.sublet"));
                            add(new SelectOption("Hyr studentrum", ProductTypes.STUDENT.toString()));
                            //add(new SelectOption("Är inneboende", ProductTypes.LODGER.toString()));

                        }}
                ));

        createMessage("message.lghtyp.sublet",
                new MessageBodySingleSelect("Okej! Är lägenheten du hyr i andra hand en hyresrätt eller bostadsrätt?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Hyresrätt", ProductTypes.SUBLET_RENTAL.toString()));
                            add(new SelectOption("Bostadsrätt", ProductTypes.SUBLET_BRF.toString()));
                        }}
                ));
        
        // ALTERNATIVT KAN DESSA SVARSALTERNATIV GÖRAS TILL SCROLL ELLER SÅ?

        createMessage("message.pers", new MessageBodyNumber("Hoppas du trivs! Bor du själv eller med andra? Fyll i hur många som bor i lägenheten"));
        setExpectedReturnType("message.pers", new HouseholdMemberNumber());

        createMessage("message.sakerhet",
                new MessageBodyMultipleSelect("Tack! Finns någon av de här säkerhetsgrejerna i lägenheten?",
                        new ArrayList<SelectItem>(){{
                            add(new SelectOption("Brandvarnare", "safety.alarm"));
                            add(new SelectOption("Brandsläckare", "safety.extinguisher"));
                            add(new SelectOption("Säkerhetsdörr", "safety.door"));
                            add(new SelectOption("Gallergrind", "safety.gate"));
                            add(new SelectOption("Inbrottslarm", "safety.burglaralarm"));
                            add(new SelectOption("Inget av dessa", "safety.none", false, true));
                        }}
                ));

        createMessage("message.forsakringidag",
                new MessageBodySingleSelect("Då är vi snart klara! Har du någon hemförsäkring idag?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ja", "message.forsakringidagja"));
                            add(new SelectOption("Nej", "message.forslag")); //Create product

                        }}
                ));

        createMessage("message.forsakringidagja",
                new MessageBodySingleSelect("Klokt av dig att redan ha försäkring! Vilket försäkringsbolag har du?",
                        new ArrayList<SelectItem>(){{
                        	add(new SelectOption("If", "if"));
                            add(new SelectOption("Folksam", "Folksam"));
                            add(new SelectOption("Trygg-Hansa", "Trygg-Hansa"));
                            add(new SelectOption("Länsförsäkringar", "Länsförsäkringar"));
                            //add(new SelectOption("Moderna", "Moderna"));
                            add(new SelectOption("Annat bolag", "message.annatbolag"));
                            add(new SelectOption("Ingen aning", "message.bolag.vetej"));

                        }}
                ));

        createMessage("message.bolag.vetej",new MessageBodyParagraph("Inga problem, det kan vi ta senare"));
        addRelay("message.bolag.vetej", "message.forslag");

        createMessage("message.annatbolag", new MessageBodyText("Okej, vilket försäkringsbolag har du?"),2000);
        //setExpectedReturnType("message.manuellnamn", new TextInput());

        createChatMessage("message.bytesinfo",
                new MessageBodySingleSelect("Ja, ibland är det dags att prova något nytt. De kommer nog förstå\f"
                		+ "Om du blir medlem hos mig sköter jag bytet åt dig. Så när din gamla försäkring går ut, flyttas du automatiskt till din nya hos mig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag förstår", "message.forslag")); //Create product
                            add(new SelectOption("Förklara mer", "message.bytesinfo3"));
                        }}
                ));


        createChatMessage("message.bytesinfo3",
                new MessageBodySingleSelect("Självklart!\f"
                		+ "Oftast har du ett tag kvar på bindningstiden på din gamla försäkring\f"
                		+ "Om du väljer att byta till Hedvig så hör jag av mig till ditt försäkringsbolag och meddelar att du vill byta försäkring så fort bindningstiden går ut\f"
                		+ "Till det behöver jag en fullmakt från dig som du skriver under med mobilt BankID \f"
                		+ "Sen börjar din nya försäkring gälla direkt när den gamla går ut\f"
                		+ "Så du behöver aldrig vara orolig att gå utan försäkring efter att du skrivit på med mig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Okej!", "message.forslag")); //Create product
                        }}
                ));

        createMessage("message.forslag", new MessageBodyParagraph("Okej! Nu har jag allt för att ge dig ett förslag. Ska bara räkna lite..."),2000);
        createMessage("message.forslag2",
                new MessageBodySingleSelect("Sådärja!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectLink("Visa mig förslaget", "message.forslag.dashboard", "Offer", null, null, false  ));

                        }}
                ));
        addRelay("message.forslag","message.forslag2");
        
        createMessage("message.forslagpop",
                new MessageBodySingleSelect("(FÖRSLAG VISAS I POP-UP. I POP-UP FINNS NEDAN ALTERNATIV SOM TAR EN TILLBAKA TILL CHATTEN NÄR EN VALT)",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill fundera", "message.fundera"));

                        }}
                ));

        createMessage("message.fundera",
                new MessageBodySingleSelect("Är det kanske något av det här du funderar kring?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Är Hedvig tryggt?", "message.tryggt"));
                            add(new SelectOption("Ger Hedvig ett bra skydd?", "message.skydd"));
                            add(new SelectOption("Är Hedvig prisvärt?", "message.pris"));
                            add(new SelectOption("Jag har en annan fråga", "message.frifraga"));

                        }}
                ));

        createChatMessage("message.tryggt",
                new MessageBodySingleSelect(""
                		+ "Jag har en trygghetspartner som är en av världens största återförsäkringskoncerner\fDe är där för mig, så jag alltid kan vara där för dig\fJag är självklart också auktoriserad av Finansinspektionen" + emoji_mag,
                        new ArrayList<SelectItem>() {{
                        	add(new SelectLink("Visa förslaget igen", "message.forslag.dashboard", "Offer", null, null, false  ));
                            add(new SelectOption("Jag har en annan fråga", "message.frifraga"));
                        }}
                ));
        
        createChatMessage("message.skydd",
                new MessageBodySingleSelect(""
                		+ "Med mig har du samma grundskydd som vanliga försäkringsbolag\fUtöver det ingår alltid drulle, alltså till exempel om du tappar din telefon i golvet och den går sönder, och ett bra reseskydd",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectLink("Visa förslaget igen", "message.forslag.dashboard", "Offer", null, null, false  ));
                        	add(new SelectOption("Jag har en annan fråga", "message.frifraga"));
                            //add(new SelectOption("Jag vill bli medlem", "message.forslag"));
                        }}
                ));
        
        createMessage("message.frifragatack",
                new MessageBodySingleSelect("Tack! Jag hör av mig inom kort",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectLink("Visa förslaget igen", "message.forslag.dashboard", "Offer", null, null, false  ));
                        	//add(new SelectOption("Jag vill bli medlem", "message.forslag"));
                            add(new SelectOption("Jag har fler frågor", "message.frifraga"));

                        }}
                ));

        createChatMessage("message.uwlimit.housingsize",
                new MessageBodyText("Det var stort! För att kunna försäkra så stora lägenheter behöver vi ta några grejer över telefon\fVad är ditt nummer?")
                );
        
        createChatMessage("message.uwlimit.householdsize",
                new MessageBodyText("Okej! För att kunna försäkra så många i samma lägenhet behöver vi ta några grejer över telefon\fVad är ditt nummer?")
                );
        
        createChatMessage("message.pris",
                new MessageBodySingleSelect("Grundskyddet som jag ger är också bredare än det du oftast får på annat håll\fOch det jag prioriterar allra mest är att vara där på dina villkor. Jag utvecklas alltid för att vara så snabb, smidig och smart som möjligt",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectLink("Visa förslaget igen", "message.forslag.dashboard", "Offer", null, null, false  ));
                        	//add(new SelectOption("Jag vill bli medlem", "message.forslag"));
                            add(new SelectOption("Jag har fler frågor", "message.frifraga"));

                        }}
                ));

//DET VORE SNYGGT OM FUNDERASVARSALTERNATIVEN I medlem.fundera-trädet KUNDE FÖRSVINNA/ÄNDRAS BEROENDE PÅ VILKA EN KLICKAT PÅ! =)


        //(FUNKTION: FYLL I BANKNAMN) = SCROLL MED DE VANLIGASTE BANKERNA SAMT "ANNAN BANK"

        createMessage("message.mail", new MessageBodyText("Tackar.\nOch din mailadress så jag kan skicka en bekräftelse när vi skrivit på?"));

        //(FUNKTION: FYLL I MAILADRESS) = FÄLT
        setExpectedReturnType("message.mail", new EmailAdress());


        createMessage("message.bankid.error.expiredTransaction", new MessageBodyParagraph(BankIDStrings.expiredTransactionError),1500);

        createMessage("message.bankid.error.certificateError", new MessageBodyParagraph(BankIDStrings.certificateError),1500);


        createMessage("message.bankid.error.userCancel", new MessageBodyParagraph(BankIDStrings.userCancel),1500);


        createMessage("message.bankid.error.cancelled", new MessageBodyParagraph(BankIDStrings.cancelled),1500);


        createMessage("message.bankid.error.startFailed", new MessageBodyParagraph(BankIDStrings.startFailed),1500);

        createMessage("message.kontraktbbbbbb",
                new MessageBodySingleSelect("Tack igen! Och nu till det stora ögonblicket. Här har du allt som vi sagt samlat. Läs igenom och skriv på med ditt BankID för att godkänna din nya försäkring",
                        new ArrayList<SelectItem>() {{
                            add(new SelectLink("Visa kontraktet", "message.kontraktpop", null, null, gatewayUrl + "/insurance/contract/{PRODUCT_ID}", false));

                        }}
                ));

        createMessage("message.kontrakt.great", new MessageBodyParagraph("Härligt!"), 1000);
        addRelay("message.kontrakt.great","message.kontrakt");


        createMessage("message.kontrakt.signError", new MessageBodyParagraph("Hmm nu blev något fel! Vi försöker igen " + emoji_flushed_face), 1000);
        addRelay("message.kontrakt.signError","message.kontrakt");

        createMessage("message.kontrakt.signProcessError", new MessageBodyParagraph("Vi försöker igen " + emoji_flushed_face), 1000);
        addRelay("message.kontrakt.signProcessError","message.kontrakt");

        createMessage("message.kontrakt",
                new MessageBodySingleSelect("Då är det bara att signera, sen är vi klara",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectOption("Signera med BankId", "message.kontraktpop.startBankId"));
                        	//add(new SelectLink("Läs igenom", "message.kontrakt", null, null, gatewayUrl + "/insurance/contract/{PRODUCT_ID}", false));
                        }}
                ),
                (m, userContext) -> {

                    if(m.getSelectedItem().value.equals("message.kontrakt")) {
                        m.text = m.getSelectedItem().text;
                        return m.getSelectedItem().value;
                    }else {
                        UserData ud = userContext.getOnBoardingData();

                        Optional<BankIdSignResponse> signData;

                        String signText;
                        if(ud.getCurrentInsurer() != null) {
                            signText = "Jag har tagit del av förköpsinformation och villkor och bekräftar genom att signera att jag vill byta till Hedvig när min gamla försäkring går ut. Jag ger också  Hedvig fullmakt att byta försäkringen åt mig.";
                        } else {
                            signText = "Jag har tagit del av förköpsinformation och villkor och bekräftar genom att signera att jag skaffar en försäkring hos Hedvig.";
                        }

                        signData = memberService.sign(ud.getSSN(), signText, userContext.getMemberId());

                        if (signData.isPresent()) {
                            userContext.startBankIdSign(signData.get());
                        } else {
                            log.error("Could not start signing process.");
                            return "message.kontrakt.signError";
                        }
                        return "";
                    }
                });

        createMessage("message.kontraktpop.bankid.collect",
                new MessageBodyBankIdCollect( "{REFERENCE_TOKEN}")
        );

        createMessage("message.kontraktpop.startBankId",
                new MessageBodySingleSelect("Då återstår bara signeringen",
                        new ArrayList<SelectItem>() {{
                            add(new SelectLink("Signera", "message.kontraktpop.bankid.collect", null, "bankid:///?autostarttoken={AUTOSTART_TOKEN}&redirect={LINK_URI}", null, false));
                        }}
                ),
                (m, uc) -> {
                    UserData obd = uc.getOnBoardingData();
                    if(m.getSelectedItem().value.equals("message.kontraktpop.bankid.collect"))
                    {
                        obd.setBankIdMessage("message.kontraktpop.startBankId");
                    }

                    return "";
                });

        setupBankidErrorHandlers("message.kontraktpop.startBankId", "message.kontrakt");

        createChatMessage("message.kontraktklar",
                new MessageBodySingleSelect("Hurra! "+ emoji_tada + " Välkommen som medlem!"+
        "\fJag skickar en bekräftelse till din mail. Visst stämmer det att du har {EMAIL}?",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectOption("Ja", "message.onboarding.end"));
                        	add(new SelectOption("Nej", "message.kontrakt.email"));
                            //add(new SelectLink("Börja utforska", "onboarding.done", "Dashboard", null, null,  false));
                        }}
                ));
        
        createMessage("message.kontrakt.email", new MessageBodyText("Ok, är din mailadress? "));
        setExpectedReturnType("message.kontrakt.email", new EmailAdress());
        


        createMessage("message.avslutvalkommen",
                new MessageBodySingleSelect("Hej så länge och ännu en gång, varmt välkommen!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectLink("Nu utforskar jag", "onboarding.done", "Dashboard", null, null, false));
                        }}
                ));

        //(FUNKTION: OMSTART) = VORE TOPPEN MED EN FUNKTION SOM GÖR ATT FOLK KAN BÖRJA CHATTA FRÅN BÖRJAN IGEN, SÅ CHATTEN KAN BLI EN LOOP OCH GÖRAS OM IGEN OCH VISAS FÖR ANDRA PERSONER ÄN MEDLEMMEN

        createMessage("message.avslutok",
                new MessageBodySingleSelect("Okej! Trevligt att chattas, ha det fint och hoppas vi hörs igen!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag vill starta om chatten", "message.onboardingstart"));

                        }}
                ));

        createMessage("message.quote.close.old",
                new MessageBodySingleSelect("Verkade förslaget intressant så välj OK så fortsätter vi prata sen",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectLink("Visa förslaget igen", "message.forslag.dashboard", "Offer", null, null, false  ));
                        	
                        }}
                ));

        createChatMessage("message.quote.close",
                new MessageBodySingleSelect("Du kanske undrade över något"
                		+"\fNågot av det här kanske?",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Är Hedvig tryggt?", "message.tryggt"));
                            add(new SelectOption("Ger Hedvig ett bra skydd?", "message.skydd"));
                            add(new SelectOption("Är Hedvig prisvärt?", "message.pris"));
                            add(new SelectOption("Jag har en annan fråga", "message.frifraga"));
                            add(new SelectLink("Visa förslaget igen", "message.forslag.dashboard", "Offer", null, null, false  ));

                        }}
                ));
        
        createMessage("message.bikedone", new MessageBodyText("Nu har du sett hur det funkar..."));

        createMessage("error", new MessageBodyText("Oj nu blev något fel..."));

    }

    private void setupBankidErrorHandlers(String messageId) {
        setupBankidErrorHandlers(messageId, null);
    }

    private void setupBankidErrorHandlers(String messageId, String relayId) {

        if(relayId == null) {
            relayId = messageId;
        }

        createMessage(messageId + ".bankid.error.expiredTransaction", new MessageBodyParagraph(BankIDStrings.expiredTransactionError),1500);
        addRelay(messageId + ".bankid.error.expiredTransaction", relayId);

        createMessage(messageId + ".bankid.error.certificateError", new MessageBodyParagraph(BankIDStrings.certificateError),1500);
        addRelay(messageId + ".bankid.error.certificateError", relayId);

        createMessage(messageId + ".bankid.error.userCancel", new MessageBodyParagraph(BankIDStrings.userCancel),1500);
        addRelay(messageId + ".bankid.error.userCancel", relayId);

        createMessage(messageId + ".bankid.error.cancelled", new MessageBodyParagraph(BankIDStrings.cancelled),1500);
        addRelay(messageId + ".bankid.error.cancelled", relayId);

        createMessage(messageId + ".bankid.error.startFailed", new MessageBodyParagraph(BankIDStrings.startFailed),1500);
        addRelay(messageId + ".bankid.error.startFailed", relayId);

        createMessage(messageId + ".bankid.error.invalidParameters", new MessageBodyParagraph(BankIDStrings.userCancel),1500);
        addRelay(messageId + ".bankid.error.invalidParameters", relayId);
    }

    @Override
    public void init(UserContext userContext) {
        log.info("Starting onboarding conversation");
        if(userContext.getDataEntry("{SIGNED_UP}") == null) {
            startConversation(userContext, "message.onboardingstart"); // Id of first message
        }else{
            startConversation(userContext, "message.activate.ok.b"); // Id of first message
        }

    }

	@Override
	public void init(UserContext userContext, String startMessage) {
        log.info("Starting onboarding conversation with message:" + startMessage);
        if(startMessage.equals("message.start.login")) {
            userContext.putUserData(LOGIN, "true");
        }
        startConversation(userContext, startMessage); // Id of first message
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

    private String getText(MessageBodySingleSelect body){

        for(SelectItem o : body.choices){
            if(SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected){
                return SelectOption.class.cast(o).text;
            }
        }

        return "";
    }
    
    public ArrayList<String> getValue(MessageBodyMultipleSelect body){
        ArrayList<String> selectedOptions = new ArrayList<>();
        for(SelectItem o : body.choices){
            if(SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected){
                selectedOptions.add(SelectOption.class.cast(o).value);
            }
        }
        return selectedOptions;
    }

    // ------------------------------------------------------------------------------- //
    @Override
    public void recieveEvent(EventTypes e, String value, UserContext userContext, MemberChat memberChat){

        switch(e){
            // This is used to let Hedvig say multiple message after another
            case MESSAGE_FETCHED:
                log.info("Message fetched:" + value);
                
                // New way of handeling relay messages
                String relay = getRelay(value);
                if(relay!=null){
                    completeRequest(relay, userContext, memberChat);
                }
                if(value.equals("message.forslag")) {
                    completeOnboarding(userContext);
                }
                break;
            case ANIMATION_COMPLETE:
                switch(value){
                    case "animation.bike":
                        completeRequest("message.bikedone", userContext, memberChat);
                        break;
                }
                break;
            case MODAL_CLOSED:
                switch(value){
                    case "quote":
                        completeRequest("message.quote.close", userContext, memberChat);
                        break;
                }
                break;
            case MISSING_DATA:
                switch(value){
                    case "bisnode":
                        completeRequest("message.missing.bisnode.data", userContext, memberChat);
                        break;
                }
                break;
        }
    }

    private void completeOnboarding(UserContext userContext) {
        String productId = this.productPricingService.createProduct(userContext.getMemberId(), userContext.getOnBoardingData());
        userContext.getOnBoardingData().setProductId(productId);
        this.memberService.finalizeOnBoarding(userContext.getMemberId(), userContext.getOnBoardingData());
    }

    @Override
    public void recieveMessage(UserContext userContext, MemberChat memberChat, Message m) {
        log.info("recieveMessage:" + m.toString());

        String nxtMsg = "";

        if(!validateReturnType(m,userContext, memberChat)){return;}
        
        // Lambda
        if(this.hasSelectItemCallback(m.id) && m.body.getClass().equals(MessageBodySingleSelect.class)) {
            //MessageBodySingleSelect body = (MessageBodySingleSelect) m.body;
            nxtMsg = this.execSelectItemCallback(m.id, (MessageBodySingleSelect) m.body, userContext);
            addToChat(m, userContext);
        }

        UserData onBoardingData = userContext.getOnBoardingData();
        
        String selectedOption = (m.body.getClass().equals(MessageBodySingleSelect.class))?
        		getValue((MessageBodySingleSelect)m.body):null;
        		
		String selectedText = (m.body.getClass().equals(MessageBodySingleSelect.class))?
        		getText((MessageBodySingleSelect)m.body):null;
        		
        if(selectedOption != null){ // TODO: Think this over
	        // Check the selected option first...
	        switch(selectedOption){
		        case "message.signup.checkposition":
		        	log.info("Checking position...");
		            // We do not have the users email
		            if(!(onBoardingData.getEmail()!=null && !onBoardingData.getEmail().equals(""))){
		            	nxtMsg = "message.signup.email";
		            }else{ // Update position if there is a code
		            	userContext.putUserData("{SIGNUP_POSITION}", Objects.toString(getSignupQueuePosition(onBoardingData.getEmail())));
		            }
		        break;
		        case "message.mockme":
		        	log.info("Mocking data...");
		            m.body.text = "Mocka mina uppgifter tack!";
		            userContext.clearContext();
		            userContext.mockMe();	        
		            addToChat(m, userContext);
		        break;
	        }
        }
        
        // ... and then the incomming message id
        switch (getMessageId(m.id)) {
	        case "message.lghtyp": {
	        	SelectItem item = ((MessageBodySingleSelect)m.body).getSelectedItem();
	        	
	        	// Additional question for sublet contracts
	        	m.body.text = "Jag " + item.text.toLowerCase();
	        	if(item.value.equals("message.lghtyp.sublet")){
	        		nxtMsg = "message.lghtyp.sublet";
	        		break;
	        	}	        	
	        	else{
	        		UserData obd = userContext.getOnBoardingData();
		            obd.setHouseType(item.value);
		            nxtMsg = "message.pers";
	        	}
	            break;
	        }
	        case "message.lghtyp.sublet": {
	        	SelectItem item = ((MessageBodySingleSelect)m.body).getSelectedItem();
	            UserData obd = userContext.getOnBoardingData();
	            obd.setHouseType(item.value);
	            m.body.text = item.text;
	            nxtMsg = "message.pers";
	            break;
	        }
            case "message.student":
                SelectItem sitem2 = ((MessageBodySingleSelect)m.body).getSelectedItem();
                if (sitem2.value.equals("message.studentja")) {
                    log.info("Student detected...");
                	userContext.putUserData("{STUDENT}", "1");
                }
                break;

            case "message.audiotest":
            case "message.phototest":
            	nxtMsg = "message.fileupload.result";
            	break;
            case "message.forslagstart":
                onBoardingData.setHouseType(((MessageBodySingleSelect)m.body).getSelectedItem().value);
                break;
            case "message.kontrakt.email":
                onBoardingData.setEmail(m.body.text);
                m.body.text = m.body.text;
                addToChat(m, userContext);
                endConversation(userContext);
                return;


            case "message.nyhetsbrev":
                onBoardingData.setNewsLetterEmail(m.body.text);
                nxtMsg = "message.nagotmer";
                break;
            case "message.signup.email" :
            case "message.waitlist":
            	// Logic goes here
            	String userEmail = m.body.text.toLowerCase();
            	onBoardingData.setEmail(userEmail);
            	m.body.text = userEmail;
            	addToChat(m, userContext);
            	
            	
            	// --------- Logic for user state ------------- //
            	Optional<SignupCode> existingSignupCode = findSignupCodeByEmail(userEmail);
            	
            	// User already has a signup code
            	if(existingSignupCode.isPresent()){
            		SignupCode esc = existingSignupCode.get();
            		if(esc.getActive()){ // User should have got an activation code
            			nxtMsg = "message.waitlist.user.alreadyactive";
            		}else{
            			nxtMsg = "message.signup.checkposition";
            		}
            	}else{
	            	SignupCode sc = createSignupCode(userEmail);
	            	userContext.putUserData("{SIGNUP_CODE}", sc.code);
	            	// TODO: Remove constant when list is up
	            	//userContext.putUserData("{SIGNUP_POSITION}", new Integer(90 + getSignupQueuePosition(userEmail)).toString());
	            	nxtMsg = "message.signup.checkposition";
            	}
            	userContext.putUserData("{SIGNUP_POSITION}", Objects.toString(getSignupQueuePosition(userEmail)));
                
                break;
            case "message.signup.flerval":
            	// TODO: Remove constant when list is up
            	userContext.putUserData("{SIGNUP_POSITION}", Objects.toString(getSignupQueuePosition(onBoardingData.getEmail())));
                break;
            case "message.waitlist.user.alreadyactive":
            case "message.activate.nocode.tryagain":
            case "message.activate":
            	// Logic goes here
            	String userCode = m.body.text.toUpperCase();
            	m.body.text = userCode;
            	addToChat(m, userContext);
            	nxtMsg = validateSignupCode(userCode, userContext);
                //nxtMsg = "message.activate.ok.a";
                break;
            case "message.uwlimit.housingsize":
            case "message.uwlimit.householdsize":
            	userContext.putUserData("{UWLIMIT_PHONENUMBER}", m.body.text);
                addToChat(m, userContext);
                nxtMsg = "message.uwlimit.tack";
                break;
            case "message.tipsa":
                onBoardingData.setRecommendFriendEmail(m.body.text);
                nxtMsg = "message.nagotmer";
                break;
            case "message.frifraga":
            	userContext.putUserData("{ONBOARDING_QUESTION}", m.body.text);
                addToChat(m, userContext);
                nxtMsg = "message.frifragatack";
                break;
            case "message.pers":
                int nr_persons = getValue((MessageBodyNumber)m.body);
                onBoardingData.setPersonInHouseHold(nr_persons);
                if(nr_persons==1){ m.body.text = "Jag bor själv"; }
                else{ m.body.text = "Vi är " + nr_persons + " i hushållet"; }
                addToChat(m, userContext);
                
                if(nr_persons > 6){
                	nxtMsg = "message.uwlimit.householdsize";
                	break;
                }
                nxtMsg = "message.sakerhet";
                break;
            case "message.kvadrat":
                String kvm = m.body.text;
                onBoardingData.setLivingSpace(Float.parseFloat(kvm));
                m.body.text = kvm + " kvm";
                addToChat(m, userContext);
                if(Integer.parseInt(kvm) > 250){
                	nxtMsg = "message.uwlimit.housingsize";
                	break;
                }
                if(onBoardingData.getAge() > 0 && onBoardingData.getAge() < 27) {
                    nxtMsg = "message.student";
                } else {
                    nxtMsg = "message.lghtyp";
                }

                break;                             
            case "message.manuellnamn":
                onBoardingData.setFirstName(m.body.text);
                addToChat(m, userContext);
                nxtMsg = "message.manuellfamilyname";
                break;
            case "message.manuellfamilyname":
            	onBoardingData.setFamilyName(m.body.text);
                addToChat(m, userContext);
                nxtMsg = "message.manuellpersonnr";
                break;
            case "message.manuellpersonnr":
                onBoardingData.setSSN(m.body.text);
                addToChat(m, userContext);
                nxtMsg = "message.varborduadress";
                break;
            case "message.bankidja.noaddress":
            case "message.varbordufeladress":
            case "message.varborduadress":
                onBoardingData.setAddressStreet(m.body.text);
                addToChat(m, userContext);
                nxtMsg = "message.varbordupostnr";
                break;
            case "message.varbordupostnr":
                onBoardingData.setAddressZipCode(m.body.text);
                addToChat(m, userContext);
                nxtMsg = "message.kvadrat";
                break;                
            case "message.mockme":
                nxtMsg = m.body.text.toLowerCase();
                m.body.text = "Jag vill gå till " + nxtMsg + " tack";
                addToChat(m, userContext);
                break;
            case "message.varbordu":
                onBoardingData.setAddressStreet(m.body.text);
                addToChat(m, userContext);
                nxtMsg = "message.kvadrat";
                break;
            case "message.mail":
                onBoardingData.setEmail(m.body.text);
                addToChat(m, userContext);
                nxtMsg = "message.kontrakt";
                break;
            case "message.sakerhet":
                MessageBodyMultipleSelect body = (MessageBodyMultipleSelect)m.body;

                if(body.getNoSelectedOptions() == 0) {
                    m.body.text = "Jag har inga säkerhetsgrejer";
                }
                else{
                    m.body.text = String.format("Jag har %s", body.selectedOptionsAsString());
                    for(SelectOption o : body.selectedOptions()){
                        onBoardingData.addSecurityItem(o.value);
                    }
                }
                addToChat(m, userContext);
                nxtMsg = "message.forsakringidag";
                break;

            //case "message.bytesinfo":
            case "message.bytesinfo2":
            case "message.forsakringidag":
            case "message.missingvalue":
            case "message.forslag2":

                SelectItem item = ((MessageBodySingleSelect)m.body).getSelectedItem();

                /*
                 * Check if there is any data missing. Keep ask until Hedvig has got all info
                 * */
                String missingItems = userContext.getMissingDataItem();
                if(missingItems!=null){
                	
                    createMessage("message.missingvalue", new MessageBodyText(
                            "Oj, nu verkar det som om jag saknar lite viktig information." + missingItems));
                    
                    m.body.text = item.text;
                    nxtMsg = "message.missingvalue";
                    addToChat(m, userContext);
                    addToChat(getMessage("message.missingvalue"), userContext);
                    break;
                }
                else if(m.id.equals("message.missingvalue") || item.value.equals("message.forslag2")) {
                    completeOnboarding(userContext);
                }
                break;
            case "message.annatbolag":
            	String _comp = m.body.text;
                userContext.putUserData("{INSURANCE_COMPANY_TODAY}", _comp);
                m.body.text = "Idag har jag " + _comp;
                nxtMsg = "message.bytesinfo";
                addToChat(m, userContext);
                break;
            case "message.forsakringidagja":
                String comp = getValue((MessageBodySingleSelect)m.body);
                if(!comp.startsWith("message.")){
	                userContext.putUserData("{INSURANCE_COMPANY_TODAY}", comp);
	                m.body.text = "Idag har jag " + comp;
	                nxtMsg = "message.bytesinfo";
	                addToChat(m, userContext);
                }
                break;
            case "message.forslagstart3":
                String selectedValue = getValue((MessageBodySingleSelect)m.body);
                addToChat(m, userContext);
                break;
                
            case "message.bankid.start.manual":
                String ssn =  m.body.text;

                Optional<BankIdAuthResponse> ssnResponse = memberService.auth(ssn);


                if(!ssnResponse.isPresent()) {
                    log.error("Could not start bankIdAuthentication!");
                    nxtMsg = "message.bankid.start.manual.error";
                }else{
                    userContext.startBankIdAuth(ssnResponse.get());
                }


                if(nxtMsg.equals("")) {
                    nxtMsg = "message.bankid.autostart.respond";
                }

                addToChat(m, userContext);
                break;

            case "message.fetch.account.complete":
                SelectItem it = ((MessageBodySingleSelect)m.body).getSelectedItem();
                userContext.getAutogiroData().setSelecteBankAccount(Integer.parseInt(it.value));
                nxtMsg = "message.kontrakt";
                break;

            case "message.kontrakt":
                completeOnboarding(userContext);
                break;

            case "message.kontraktklar":

                m.body.text = ((MessageBodySingleSelect)m.body).getSelectedItem().text;
                addToChat(m, userContext);
                endConversation(userContext);
                return;
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
                    addToChat(m, userContext);
                    nxtMsg = o.value;
                }
            }
        }

        completeRequest(nxtMsg, userContext, memberChat);

    }

    private void endConversation(UserContext userContext) {
        userContext.completeConversation(this.getClass().toString());
        userContext.startConversation(conversationFactory.createConversation(TrustlyConversation.class));
    }

    /*
     * Generate next chat message or ends conversation
     * */
    @Override
    public void completeRequest(String nxtMsg, UserContext userContext, MemberChat memberChat){

        switch(nxtMsg){
        	case "message.medlem":
            case "message.bankid.start":
            case "message.lagenhet":
                Optional<BankIdAuthResponse> authResponse = memberService.auth(userContext.getMemberId());

                if(!authResponse.isPresent()) {
                    log.error("Could not start bankIdAuthentication!");

                    nxtMsg = "message.onboardingstart";
                }else{
                    BankIdAuthResponse bankIdAuthResponse = authResponse.get();
                    userContext.startBankIdAuth(bankIdAuthResponse);
                }

                break;
            case "onboarding.done":
                //userContext.onboardingComplete(true);
                break;
            case "":
                log.error("I dont know where to go next...");
                nxtMsg = "error";
                break;
        }

        super.completeRequest(nxtMsg, userContext, memberChat);
    }

    @Override
    void addToChat(Message m, UserContext userContext) {
        if(m.body.getClass() == MessageBodySingleSelect.class) {
            MessageBodySingleSelect mss = (MessageBodySingleSelect) m.body;

            // Do not show activation option on web
            if(userContext.getDataEntry("{WEB_USER}").equals("TRUE")){
                mss.removeItemIf( x->x instanceof SelectOption && ((SelectOption)x).value.equals("message.activate"));
            }
        }

        super.addToChat(m,userContext);
    }

    @Override
    public void bankIdAuthComplete(UserContext userContext) {

        if(userContext.getOnBoardingData().getUserHasSigned()) {
            userContext.completeConversation(this.getClass().getName());
            Conversation mc = conversationFactory.createConversation(MainConversation.class);
            userContext.startConversation(mc);
        }
        else if(userContext.getDataEntry(LOGIN) != null) {
            userContext.removeDataEntry(LOGIN);
            addToChat(getMessage("message.membernotfound"), userContext);
        }
        else {
            addToChat(getMessage("message.bankidja"), userContext);
        }
    }

    @Override
    public void bankIdAuthCompleteNoAddress(UserContext uc) {
        addToChat(getMessage("message.bankidja.noaddress"), uc);
    }

    @Override
    public void bankIdAuthGeneralCollectError(UserContext userContext) {
        addToChat(getMessage("message.bankid.error"), userContext);
        String bankIdStartMessage = userContext.getOnBoardingData().getBankIdMessage();
        addToChat(getMessage(bankIdStartMessage), userContext);
    }


    public void quoteAccepted(UserContext userContext) {
        addToChat(getMessage("message.kontrakt.great"), userContext);
    }

    @Override
    public void memberSigned(String referenceId, UserContext userContext) {
        Boolean singed = userContext.getOnBoardingData().getUserHasSigned();

        if(!singed) {
            log.info("Onboarding complete");
            addToChat(getMessage("message.kontraktklar"), userContext);
            userContext.getOnBoardingData().setUserHasSigned(true);
            userContext.completeConversation(OnboardingConversationDevi.class.toString());
        }
    }

    @Override
    public void bankIdSignError(UserContext uc) {
        addToChat(getMessage("message.kontrakt.signError"), uc);
    }

    @Override
    public void oustandingTransaction(UserContext uc) {

    }

    @Override
    public void noClient(UserContext uc) {

    }

    @Override
    public void started(UserContext uc) {

    }

    @Override
    public void userSign(UserContext uc) {

    }

    @Override
    public void couldNotLoadMemberProfile(UserContext uc) {
        addToChat(getMessage("message.missing.bisnode.data"), uc);
    }

    @Override
    public void signalSignFailure(ErrorType errorType, String detail, UserContext uc) {
        addBankIdErrorMessage(errorType, "message.kontraktpop.startBankId", uc);
    }

    @Override
    public void signalAuthFailiure(ErrorType errorType, String detail, UserContext uc) {
        addBankIdErrorMessage(errorType,uc.getOnBoardingData().getBankIdMessage(), uc);
    }

    private void addBankIdErrorMessage(ErrorType errorType, String baseMessage, UserContext uc) {
        String errorPostfix;
        switch (errorType) {
            case EXPIRED_TRANSACTION:
                errorPostfix = ".bankid.error.expiredTransaction";
                break;
            case CERTIFICATE_ERR:
                errorPostfix = ".bankid.error.certificateError";
                break;
            case USER_CANCEL:
                errorPostfix = ".bankid.error.userCancel";
                break;
            case CANCELLED:
                errorPostfix = ".bankid.error.cancelled";
                break;
            case START_FAILED:
                errorPostfix = ".bankid.error.startFailed";
                break;
            case INVALID_PARAMETERS:
                errorPostfix = ".bankid.error.invalidParameters";
                break;
            default:
                errorPostfix = "";
        }
        final String messageID = baseMessage + errorPostfix;
        log.info("Adding bankIDerror message: {}", messageID);
        addToChat(getMessage(messageID), uc);
    }
    
    // ---------- Signup code logic ------------- //
    
    private String validateSignupCode(String code, UserContext uc){
    	log.debug("Validating signup code:" + code);
        Optional<SignupCode> sc = signupRepo.findByCode(code);

        if(sc.isPresent()){
        	uc.putUserData("{EMAIL}", sc.get().email);
        	if(sc.get().getActive()){
        		if(sc.get().getUsed()){
        			return "message.activate.code.used";
        		}
            	sc.get().setUsed(true);
            	signupRepo.saveAndFlush(sc.get());
            	uc.putUserData("{SIGNED_UP}", "true");
            	return "message.activate.ok.a";       		
        	}
        	uc.putUserData("{SIGNUP_POSITION}", Objects.toString(getSignupQueuePosition(sc.get().email)));
        	return "message.activate.notactive";
        }
        return "message.activate.nocode";
    }
    
    private SignupCode createSignupCode(String email){
    	log.debug("Generate signup code for email:" + email);
        SignupCode sc = signupRepo.findByEmail(email).orElseGet(() -> {
        	SignupCode newCode = new SignupCode(email);
            signupRepo.save(newCode);
            return newCode;
        });
        signupRepo.saveAndFlush(sc);
        try {
            this.memberService.sendSignupMail(email, sc.externalToken);
        }catch (Exception ex) {
            log.error("Could not send emailrequest to memberService: ", ex);
        }


        publisher.publishEvent(new SignedOnWaitlistEvent(email));

        return sc;
    }
    
    private Optional<SignupCode> findSignupCodeByEmail(String email){
    	return signupRepo.findByEmail(email);
    }
    
    private int getSignupQueuePosition(String email){
        ArrayList<SignupCode> scList = (ArrayList<SignupCode>) signupRepo.findAllByOrderByDateAsc();
        int pos = 1;
        for(SignupCode sc : scList){
        	if(!sc.used){
        		log.debug(sc.code + "|" + sc.email + "(" + sc.date+"):" + (pos));
        		if(sc.email.equals(email)){
        			return 90 + pos;
        		}
        		pos++;
        	}
        }
        return -1;
    }

}