import os
import sys
from loguru import logger
from app.util.config import config


def get_logger():
    """Configure and return a Loguru logger."""

    log_level = config.LOG_LEVEL if config.LOG_LEVEL else "DEBUG"   # e.g., "DEBUG", "INFO"
    store_logs = config.STORE_LOGS if config.STORE_LOGS else False  # Boolean flag to determine if logs should be stored
    log_format = "{time:YYYY-MM-DD HH:mm:ss} | {level} | {message}"  # Clean log format

    # Remove default Loguru handler to avoid duplicates
    logger.remove()

    # Always add a console handler
    logger.add(sys.stderr, format=log_format, level=log_level)

    # Add file logging if STORE_LOGS is enabled
    if store_logs:
        log_file_path = os.path.join(config.LOG_DIR, "app.log")  # Example: logs/app.log
        logger.add(log_file_path, format=log_format, level=log_level, rotation="10 MB", compression="zip")

    return logger
