package example_app;

import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

final class WorkshopLogging {
	private WorkshopLogging() {
	}

	static void configure() {
		installBouncyCastleNoiseFilter();
		Logger.getLogger("org.bouncycastle").setLevel(Level.WARNING);
		Logger.getLogger("org.bouncycastle.jsse").setLevel(Level.WARNING);
		Logger.getLogger("org.bouncycastle.jsse.provider").setLevel(Level.WARNING);
	}

	private static void installBouncyCastleNoiseFilter() {
		Handler[] handlers = Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			final Filter existingFilter = handlers[i].getFilter();
			handlers[i].setFilter(new Filter() {
				@Override
				public boolean isLoggable(LogRecord record) {
					return !isBouncyCastleInfo(record)
							&& (existingFilter == null || existingFilter.isLoggable(record));
				}
			});
		}
	}

	private static boolean isBouncyCastleInfo(LogRecord record) {
		return record != null && record.getLoggerName() != null && record.getLoggerName().startsWith("org.bouncycastle")
				&& (record.getLevel().intValue() < Level.WARNING.intValue() || isExpectedTlsCloseWarning(record));
	}

	private static boolean isExpectedTlsCloseWarning(LogRecord record) {
		if (record == null || record.getMessage() == null) {
			return false;
		}
		if (!record.getMessage().contains("Failed to write record")) {
			return false;
		}
		Throwable thrown = record.getThrown();
		return thrown instanceof java.net.SocketException
				&& thrown.getMessage() != null
				&& thrown.getMessage().toLowerCase(java.util.Locale.ENGLISH).contains("connection abort");
	}
}
