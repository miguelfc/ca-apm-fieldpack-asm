package com.ca.apm.swat.epaplugins.utils;

/**
 * Contains all constants for CA App Synthetic Monitor EPA plugin.
 * @author Guenter Grossberger - CA APM SWAT Team
 *
 */
public interface ASMProperties {

    //TODO: GG reset to true
    public static final Boolean apmcmQuiet = Boolean.valueOf(false);

    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String EMPTY_STRING = "";
    public static final String ZERO = "0";
    public static final String ONE = "1"; 
    public static final String YES = "y";
    public static final String NO = "n";
    public static final String NO_TYPE = "no type";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String UTF8 = "UTF-8";

    public static final String APMCM_PRODUCT_NAME = "App Synthetic Monitor";
    public static final String APMCM_PRODUCT_NAME_SHORT = "ASM";

    public static final String PROPERTY_FILE_NAME     = "APMCloudMonitor.properties";

    public static final String DEFAULT_LOCALE         = "en_US";
    public static final String LOCALE                 = "apmcm.locale";
    public static final String WAIT_TIME              = "apmcm.waittime";
    public static final String USER                   = "apmcm.user";
    public static final String DISPLAY_CHECKPOINTS    = "apmcm.displaycheckpoints";

    public static final String METRICS_STATS_FOLDER   = "apmcm.metrics.stats.folder";
    public static final String METRICS_CREDITS        = "apmcm.metrics.credits";
    public static final String METRICS_PUBLIC         = "apmcm.metrics.public";
    public static final String METRICS_LOGS           = "apmcm.metrics.logs";
    public static final String METRICS_STATS_RULE     = "apmcm.metrics.stats.rule";

    public static final String PROXY_HOST             = "apmcm.proxy.host";
    public static final String PROXY_PORT             = "apmcm.proxy.port";
    public static final String PROXY_USER             = "apmcm.proxy.user";
    public static final String PROXY_PASS_ENCRYPTED   = "apmcm.proxy.pass.encrypted";
    public static final String PROXY_PASS             = "apmcm.proxy.pass";

    public static final String LOCAL_TEST             = "apmcm.localtest";

    public static final String URL                    = "apmcm.URL";
    public static final String PASSWORD_ENCRYPTED     = "apmcm.pass.encrypted";
    public static final String PASSWORD               = "apmcm.pass";
    public static final String FOLDERS                = "apmcm.folders";
    public static final String SKIP_INACTIVE_FOLDERS  = "apmcm.skip_inactive.folders";
    public static final String FOLDER_PREFIX          = "apmcm.folder.";
    public static final String NUM_LOGS               = "apmcm.numlogs";

    public static final String kDefaultDelimiter = ",";
    public static final String[] kNoStringArrayProperties = new String[0];

    public static final int kBufferWaitTime = 60000;
    public static final String kJavaNetExceptionRegex =
            ".*(BindException|ConnectException|HttpRetryException|NoRouteToHostException|"
            + "ProtocolException|SocketException|SocketTimeoutException|UnknownHostException).*";
    public static final String kJsonRegex = "doCallback\\((.*)\\)([\n]*)";
    public static final String kJsonPattern = "\\p{InCombiningDiacriticalMarks}+";
    public static final String kAlgorithm = "PBEWithMD5AndDES";
    public static final String kXMLPrefix = "<?xml";
    public static final String kCreditsCategory = "Credits";
    public static final String kLogCategory = "Log";
    public static final String kPSPCategory = "Public Stats";
    public static final String kStatsCategory = "Stats";
    public static final String MESSAGE_DIGEST = "1D0NTF33LT4RDY";
    public static final String METRIC_PATH_SEPARATOR = "|";
    public static final String METRIC_NAME_SEPARATOR = ":";

    public static final String kAPMCMPSPCmd = "rule_psp";
    public static final String kAPMCMStatsCmd = "rule_stats";
    public static final String kAPMCMLoginCmd = "acct_token";
    public static final String kAPMCMLogoutCmd = "acct_logout";
    public static final String kAPMCMLogsCmd = "rule_log";
    public static final String kAPMCMCheckptsCmd = "cp_list";
    public static final String kAPMCMFoldersCmd = "fldr_get";
    public static final String kAPMCMRuleCmd = "rule_get";
    public static final String kAPMCMCreditsCmd = "acct_credits";

    public static final String kAPMCMNKeyParam = "nkey=";
    public static final String kAPMCMCallbackParam = "&callback=";
    public static final String kAPMCMFolderParam = "&folder=";
    public static final String kAPMCMNameParam = "&name=";
    public static final String kAPMCMReverseParam = "&reverse=y";
    public static final String kAPMCMNumParam = "&num=";
    public static final String kAPMCMStartDateParam = "&start_date=";
    public static final String kAPMCMAccountParam = "&acct=";
    public static final String kAPMCMFullParam = "&full=y";

    public static final String kAPMCMActive = "active";
    public static final String kAPMCMAreas = "areas";
    public static final String kAPMCMCountry = "country_name";
    public static final String kAPMCMCity = "city";
    public static final String kAPMCMCode = "code";
    public static final String kAPMCMCheckpoints = "checkpoints";
    public static final String kAPMCMCredits = "credits";
    public static final String kAPMCMResult = "result";
    public static final String kAPMCMNKey = "nkey";
    public static final String kAPMCMError = "error";
    public static final String kAPMCMErrors = "errors";
    public static final String kAPMCMFolder = "folder";
    public static final String kAPMCMFolders = "folders";
    public static final String kAPMCMRules = "rules";
    public static final String kAPMCMInfo = "info";
    public static final String kAPMCMName = "name";
    public static final String kAPMCMDescr = "descr";
    public static final String kAPMCMLoc = "loc";
    public static final String kAPMCMMonitors = "monitors";
    public static final String kAPMCMStats = "stats";
    public static final String kAPMCMColor = "color";
    public static final String kAPMCMColors = "colors";
    public static final String kAPMCMElapsed = "elapsed";
    public static final String kAPMCMType = "type";
    public static final String kAPMCMVersion = "version";
    public static final String kAPMCMOutput = "output";
    public static final String kAPMCMHarOrLog = "{\"har\": {\"log\"";
    public static final String kAPMCMUndefined = "Undefined";
    public static final String METRIC_TREE = "APM Cloud Monitor";
    public static final String MONITOR_METRIC_PREFIX = "Monitors|";
    public static final String STATUS_METRIC_PREFIX = "Status Monitoring|";
    public static final String ROOT_FOLDER = "root_folder";
    public static final String ALL_FOLDERS = "all_folders";
    public static final String ALL_RULES = "all_rules";

    public static final String apmcmCallback = "doCallback";
    public static final String apmcmMethod = "POST";
    public static final String apmcmPasswordPage = "https://dashboard.cloudmonitor.ca.com/en/change_passwd.php";

    public static final String TEST_RESULTS         = "testResults";
    public static final String RESPONSE_CODE_TAG    = "rc";
    public static final String RESPONSE_MESSAGE_TAG = "rm";
    public static final String SUCCESS_FLAG_TAG     = "s";
    public static final String ERROR_COUNT_TAG      = "ec";
    public static final String TEST_URL_TAG         = "lb";
    public static final String UNDEFINED_ASSERTION  = "Undefined Assertion";
    public static final String ASSERTION_RESULT     = "assertionResult";
    public static final String FAILURE              = "failure";
    public static final String STEP                 = "Step ";
    public static final String ASSERTION_FAILURE    = " - Assertion Failure";
    public static final String ASSERTION_ERROR      = " - Assertion Error";

    public static final String STATUS_MESSAGE       = "Status Message";
    public static final String STATUS_MESSAGE_VALUE = "Status Message Value";
    public static final String RESPONSE_CODE        = "Response Code";
    public static final String ERROR_COUNT          = "Error Count";
    public static final String ASSERTION_FAILURES   = "Assertion Failures";
    public static final String ASSERTION_ERRORS     = "Assertion Errors";
    public static final String TEST_URL             = "URL";

    public static final int apmcmAuthErrorCode = 1000;
    public static final int apmcmNormErrorCode = 1001;
    public static final int INIT_RETRIES = 10;
    public static final int THREAD_RETRIES = 10;
}
