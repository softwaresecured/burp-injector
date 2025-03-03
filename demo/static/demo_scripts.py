# Burp Injector development helper
# Do not modify the structure of this script or change the function signatures
# Do not remove the comments above and below the functions

import base64
TARGET_AREA_CONTENT = base64.b64decode("Z0FTVnBBQUFBQUFBQUFCOWxDaU1CbkJoY21GdE1aU01DM0JoY21GdE1YWmhiSFZsbEl3R2NHRnlZVzB5bEl3TGNHRnlZVzB5ZG1Gc2RXV1VqQVp3WVhKaGJUT1VqQXR3WVhKaGJUTjJZV3gxWlpTTUJuQmhjbUZ0TkpTTUMzQmhjbUZ0TkhaaGJIVmxsSXdHY0dGeVlXMDFsRTBLR293R2NHRnlZVzAzbEYyVUtJd0xZWEp5WVhsMllXeDFaVEdVakF0aGNuSmhlWFpoYkhWbE1wU01DMkZ5Y21GNWRtRnNkV1V6bEdWMUxnJTNEJTNE").decode("utf-8")
TEST_PAYLOAD_CONTENT = base64.b64decode("dGVzdCI=").decode("utf-8")


# DECODE_FUNCTION_START

import base64
import pickle
import json
from urllib.parse import unquote
def decode_target( target: str ) -> str:
    arg2pickle = base64.b64decode(unquote(target))
    arg2object = pickle.loads(arg2pickle)
    return json.dumps(arg2object)

# DECODE_FUNCTION_END

# ENCODE_FUNCTION_START

# encoder script used to encode a region of text from a targeting rule after the alteration has been injected
import base64
import json
import pickle
from urllib.parse import quote
def encode_target( target: str ) -> str:
    data = json.loads(target)
    pickled_data = pickle.dumps(data)
    return quote(base64.b64encode(pickled_data).decode("utf-8"))

# ENCODE_FUNCTION_END


# PAYLOAD_PROCESS_FUNCTION_START

# payload processing script used to alter a payload prior to injection and encoding
import json
def process_payload( payload: str ) -> str:
	processed_payload = payload
	json_encoded = json.dumps(payload)
	if json_encoded is not None and len(json_encoded) > 2:
		processed_payload= json_encoded[1:-1]
	return processed_payload

# PAYLOAD_PROCESS_FUNCTION_END

if __name__ == '__main__':
    decoded_target = decode_target(TARGET_AREA_CONTENT)
    encoded_target = encode_target(decoded_target)
    processed_payload = process_payload(TEST_PAYLOAD_CONTENT)

    print(f"Decoded target:\n{decoded_target}\n\nEncoded target:\n{encoded_target}\n\nProcessed payload:\n{processed_payload}\n")