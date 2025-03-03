package burp_injector.util;


import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp_injector.exceptions.InjectorException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for common request activities
 */
public class RequestUtil {
    /**
     * Creates a new request object using the proper protocol with a given service
     * @param request The original HttpRequest object
     * @param baseURL The baseURL that will be used to construct the service
     * @return A new HTTP request
     */
    public static HttpRequest rebuildWithService(HttpRequest request, String baseURL ) {
        HttpRequest rebuiltRequest = HttpRequest.httpRequest(request.toString()).withService(HttpService.httpService(baseURL));
        if ( rebuiltRequest.httpVersion().endsWith("2")) {
            rebuiltRequest = HttpRequest.http2Request(HttpService.httpService(baseURL),request.headers(),request.body());
        }
        return rebuiltRequest;
    }

    /**
     * Rebuilds a request with a given target area
     * @param targetArea The target area we're inserting into
     * @param captureGroup The capture group of the target are
     * @param baseRequest The base request that we're inserting into
     * @param encodedTargetArea The encoded target area that will be inserted
     * @return A new HTTP request with the encoded target area inserted
     */
    public static HttpRequest rebuildRequest(Pattern targetArea, int captureGroup, HttpRequest baseRequest, String encodedTargetArea ) throws InjectorException {
        HttpRequest modifiedRequest = null;
        Matcher m = targetArea.matcher(baseRequest.toString());
        if ( m.find() ) {
            if (m.groupCount() >= captureGroup) {
                String httpRequestString = String.format(
                        "%s%s%s",
                        baseRequest.toString().substring(0,m.start(captureGroup)),
                        encodedTargetArea,
                        baseRequest.toString().substring(m.end(captureGroup))
                );
                modifiedRequest = HttpRequest.httpRequest(httpRequestString);
                if ( baseRequest.httpVersion().endsWith("2")) {
                    modifiedRequest = HttpRequest.http2Request(baseRequest.httpService(),modifiedRequest.headers(),modifiedRequest.body());
                }

            }
            else {
                throw new InjectorException("Not enough match groups");
            }
        }
        else {
            throw new InjectorException("Could not match target area");
        }
        return modifiedRequest;
    }

    public static HttpRequest adjustContentLengthHeader( HttpRequest request ) {
        if ( request.body().length() > 0 ) {
            if ( request.hasHeader("content-length")) {
                request = request.withUpdatedHeader("content-length", String.format("%d", request.body().length()));
            }
        }
        return request;
    }
}
