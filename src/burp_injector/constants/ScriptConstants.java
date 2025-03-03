package burp_injector.constants;

/**
 * Constants used for Injector scripts ( encode / decode and payload process )
 */
public class ScriptConstants {

    // User stubs
    public static final String DECODER_SCRIPT_USER_STUB = "# decoder script used to decode a region of text from a targeting rule\ndef decode_target( target: str ) -> str:\n\tdecoded_target = target\n\treturn decoded_target";
    public static final String ENCODE_SCRIPT_USER_STUB = "# encoder script used to encode a region of text from a targeting rule after the alteration has been injected\ndef encode_target( target: str ) -> str:\n\tencoded_target = target\n\treturn encoded_target";
    public static final String PAYLOAD_PROCESS_SCRIPT_USER_STUB = "# payload processing script used to alter a payload prior to injection and encoding\ndef process_payload( payload: str ) -> str:\n\tprocessed_payload = payload\n\treturn processed_payload";

    // Headers
    public static final String DECODE_SCRIPT_HEADER = """
            import base64
            import sys""";
    public static final String ENCODE_SCRIPT_HEADER = """
            import base64
            import sys""";
    public static final String PAYLOAD_PROCESS_SCRIPT_HEADER = """
            import base64
            import sys""";
    // Footers
    public static final String DECODE_SCRIPT_FOOTER = """
            def __get_payload():
                return base64.b64decode(_INPUT_CONTENT).decode("utf-8")
            try:
                output = decode_target(__get_payload())
                print(output.strip())
            except Exception as e:
                print(e, file=sys.stderr)""";
    public static final String ENCODE_SCRIPT_FOOTER = """
            def __get_payload():
                return base64.b64decode(_INPUT_CONTENT).decode("utf-8")
            try:
                output = encode_target(__get_payload())
                print(output.strip())
            except Exception as e:
                print(e, file=sys.stderr)""";
    public static final String PAYLOAD_PROCESS_SCRIPT_FOOTER = """
            def __get_payload():
                return base64.b64decode(_INPUT_CONTENT).decode("utf-8")
            try:
                output = process_payload(__get_payload())
                print(output.strip())
            except Exception as e:
                print(e, file=sys.stderr)""";


}
