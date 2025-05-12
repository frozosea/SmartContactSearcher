from typing import Dict, Any
from app.util.logger import get_logger


class BaseService:
    """
    A base service class providing shared logic and helper methods for other services.
    """
    def __init__(self):
        self.logger = get_logger()

    def validate_required_fields(self, data: Dict[str, Any], required_fields: list) -> None:
        """
        Validates that all required fields are present in the given data dictionary.

        :param data: The dictionary containing the data to validate.
        :param required_fields: A list of required field names.
        :raises ValueError: If any required field is missing.
        """
        missing_fields = [field for field in required_fields if field not in data]
        if missing_fields:
            self.logger.error(f"Validation failed. Missing fields: {missing_fields}")
            raise ValueError(f"Missing required fields: {', '.join(missing_fields)}")

    def log_operation(self, operation_name: str, success: bool = True, details: str = "") -> None:
        """
        Logs the outcome of an operation.

        :param operation_name: The name of the operation.
        :param success: Whether the operation was successful.
        :param details: Additional details about the operation.
        """
        status = "SUCCESS" if success else "FAILURE"
        self.logger.info(f"Operation: {operation_name}, Status: {status}, Details: {details}")

    def generate_response(self, success: bool, message: str, data: Any = None) -> Dict[str, Any]:
        """
        Generates a standard response structure.

        :param success: Whether the operation was successful.
        :param message: A message describing the operation's outcome.
        :param data: Additional data to include in the response.
        :return: A dictionary containing the structured response.
        """
        return {
            "success": success,
            "message": message,
            "data": data
        }
