package com.daimlertrucksasia.it.dsc.localization.helper;

/**
 * Enumeration representing standard HTTP status codes as defined by the IETF in
 * <a href="https://datatracker.ietf.org/doc/html/rfc7231">RFC 7231</a> and related specifications.
 * <p>
 * Each enum constant encapsulates a numeric HTTP status code and a human-readable description.
 * These codes are grouped into the following categories:
 * </p>
 * <ul>
 *   <li><strong>1xx - Informational:</strong> Request received, continuing process</li>
 *   <li><strong>2xx - Success:</strong> Action successfully received, understood, and accepted</li>
 *   <li><strong>3xx - Redirection:</strong> Further action must be taken to complete the request</li>
 *   <li><strong>4xx - Client Error:</strong> The request contains bad syntax or cannot be fulfilled</li>
 *   <li><strong>5xx - Server Error:</strong> The server failed to fulfill a valid request</li>
 * </ul>
 *
 * <p>This enum also provides utility methods to retrieve the code and description,
 * as well as to resolve an enum constant from a numeric status code.</p>
 *
 * <pre>{@code
 *     HttpStatusCode status = HttpStatusCode.NOT_FOUND;
 *     int code = status.code(); // 404
 *     String desc = status.description(); // "Not Found – Resource could not be found"
 * }</pre>
 *
 * @since 1.0
 */
public enum HttpStatusCode {

    // --- 1xx Informational ---
    CONTINUE(100, "Continue – Request received, please continue"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols – Switching to new protocol as requested"),
    PROCESSING(102, "Processing – WebDAV; request is being processed"),

    // --- 2xx Success ---
    OK(200, "OK – Request succeeded"),
    CREATED(201, "Created – Resource created successfully"),
    ACCEPTED(202, "Accepted – Request accepted for processing"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information – Returned meta-information not from origin server"),
    NO_CONTENT(204, "No Content – Successful request but no content returned"),
    RESET_CONTENT(205, "Reset Content – Reset the document view"),
    PARTIAL_CONTENT(206, "Partial Content – Partial GET successful"),

    // --- 3xx Redirection ---
    MULTIPLE_CHOICES(300, "Multiple Choices – Multiple options for resource"),
    MOVED_PERMANENTLY(301, "Moved Permanently – Resource has moved permanently"),
    FOUND(302, "Found – Resource temporarily located at different URI"),
    SEE_OTHER(303, "See Other – Redirect using GET to another URI"),
    NOT_MODIFIED(304, "Not Modified – Resource not changed since last request"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect – Resource temporarily under different URI"),
    PERMANENT_REDIRECT(308, "Permanent Redirect – Resource moved permanently to new URI"),

    // --- 4xx Client Errors ---
    BAD_REQUEST(400, "Bad Request – Malformed or invalid syntax"),
    UNAUTHORIZED(401, "Unauthorized – Authentication required"),
    PAYMENT_REQUIRED(402, "Payment Required – Reserved for future use"),
    FORBIDDEN(403, "Forbidden – Server refuses to respond"),
    NOT_FOUND(404, "Not Found – Resource could not be found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed – HTTP method not allowed for this resource"),
    NOT_ACCEPTABLE(406, "Not Acceptable – Resource not capable of generating acceptable content"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout – Client did not produce a request in time"),
    CONFLICT(409, "Conflict – Request conflict with resource state"),
    GONE(410, "Gone – Resource no longer available"),
    LENGTH_REQUIRED(411, "Length Required – Content-Length header is required"),
    PRECONDITION_FAILED(412, "Precondition Failed – One or more preconditions failed"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
    URI_TOO_LONG(414, "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity – Semantic errors in request (WebDAV)"),
    TOO_MANY_REQUESTS(429, "Too Many Requests – Rate limiting in effect"),

    // --- 5xx Server Errors ---
    INTERNAL_SERVER_ERROR(500, "Internal Server Error – Unexpected server condition"),
    NOT_IMPLEMENTED(501, "Not Implemented – Server does not support functionality"),
    BAD_GATEWAY(502, "Bad Gateway – Invalid response from upstream server"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable – Server temporarily overloaded or down"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout – Upstream server timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

    private final int code;
    private final String description;

    /**
     * Constructor to initialize each enum constant with a numeric code and description.
     *
     * @param code        the numeric HTTP status code
     * @param description the textual description of the status
     */
    HttpStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Returns the numeric HTTP status code.
     *
     * @return HTTP status code as an integer
     */
    public int code() {
        return code;
    }

    /**
     * Returns the human-readable description for the HTTP status code.
     *
     * @return a string description of the HTTP status
     */
    public String description() {
        return description;
    }

    /**
     * Retrieves the enum constant that corresponds to the given HTTP status code.
     *
     * @param code the numeric HTTP status code
     * @return corresponding {@link HttpStatusCode}
     * @throws IllegalArgumentException if the code does not match any known status
     */
    public static HttpStatusCode fromCode(int code) {
        for (HttpStatusCode status : HttpStatusCode.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP status code: " + code);
    }
}
