package devarea.fr.discord.setup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.fr.discord.statics.DefaultData;
import devarea.fr.utils.Logger;
import devarea.fr.discord.utils.SnowflakeModuleSerializer;
import discord4j.common.util.Snowflake;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class InitialData {

    public static InitialData loadInitialData() {
        try {

            Logger.logTitle("Loading " + DefaultData.INITIAL_DATA_PATH + ".");

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(SnowflakeModuleSerializer.snowflakeModule);

            File initialDataFile = new File(DefaultData.INITIAL_DATA_PATH);
            InitialData initial;

            if (initialDataFile.exists())
                initial = mapper.readValue(initialDataFile, new TypeReference<>() {
                });
            else {
                initial = new InitialData();
                mapper.writeValue(initialDataFile, initial);
                Logger.logError("You need to configure " + DefaultData.INITIAL_DATA_PATH + ".");
                System.exit(0);
            }

            Logger.logMessage(DefaultData.INITIAL_DATA_PATH + " loaded.");

            return initial;
        } catch (Exception e) {
            System.err.println("Une erreur s'est produite dans le chargement de configuration.json ! : \n" + e.getMessage());
            System.exit(0);
        }

        return null;
    }


    // Admin
    public String prefix = "//";
    public boolean vanish = false;

    // Emojis
    public Snowflake yes = null;
    public Snowflake no = null;
    public Snowflake loading = null;

    // Guilds
    public Snowflake devarea = Snowflake.of("1055125166562213959");

    // Log Channels
    public Snowflake log_channel = null;
    public Snowflake logJoin_channel = null;

    // TextChannel
    public Snowflake paidMissions_channel = null;
    public Snowflake freeMissions_channel = null;
    public Snowflake presentation_channel = null;
    public Snowflake roles_channel = null;
    public Snowflake welcome_channel = null;
    public Snowflake general_channel = null;
    public Snowflake meetupVerif_channel = null;
    public Snowflake meetupAnnounce_channel = null;
    public Snowflake bump_channel = null;
    public Snowflake command_channel = null;
    public Snowflake freelance_channel = null;
    public Snowflake suggestion_channel = null;
    public Snowflake staff_channel = null;

    // Voice Channel
    public Snowflake help_voiceChannel = null;

    // Category
    public Snowflake join_category = null;
    public Snowflake missions_category = null;
    public Snowflake general_category = null;
    public Snowflake assistance_category = null;
    public Snowflake mission_follow_category = null;
    public Snowflake sanction_discuss_category = null;

    // Roles
    public Snowflake rulesAccepted_role = null;
    public Snowflake modo_role = null;
    public Snowflake admin_role = null;
    public Snowflake pingMeetup_role = null;
    public Snowflake devHelper_role = null;
    public Snowflake debutant_role = null;

    // Bots
    public Snowflake disboard_bot = null;

    // ThreadCreator list
    public ArrayList<Snowflake> channelsThreadCreator = new ArrayList<>();

    // Assets
    public HashMap<String, String> assetsImages = null;

    // -> Badges
    public HashMap<String, String> badgesImages = null;

    public String pathsMots = null;

    public InitialData() {

    }
}
