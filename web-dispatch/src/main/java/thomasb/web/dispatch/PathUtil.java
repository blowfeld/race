package thomasb.web.dispatch;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class PathUtil {
	private static final Pattern ID_PATTERN = Pattern.compile("^/([0-9a-fA-F\\-]+)(/|$)");
	
	
	public static final UUID readHandlerId(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			return null;
		}
		
		Matcher matcher = ID_PATTERN.matcher(pathInfo);
		if (matcher.matches()) {
			return UUID.fromString(matcher.group(1));
		}
		
		throw new HandlerIdException();
	}
	
	private static class HandlerIdException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
}
