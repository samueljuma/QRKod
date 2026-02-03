# QR Code Download Feature - Domain Layer

This directory contains the core domain models, interfaces, and use cases for the QR code download feature.

## Structure

### Models (`domain/model/`)
- **DownloadResult**: Result of a download operation with success status, file path, and error information
- **StorageResult**: Result of a storage operation with URI, file path, and error details
- **PermissionResult**: Result of permission requests with granted status and rationale information
- **QRDownloadContext**: Context information for download operations including bitmap and metadata
- **SavedFileMetadata**: Metadata for saved QR code files including size, MIME type, and timestamps

### Repositories/Services (`domain/repository/`)
- **StorageService**: Interface for platform-specific storage operations with API level compatibility
- **PermissionHandler**: Interface for managing Android storage permissions across different API levels

### Use Cases (`domain/usecase/`)
- **DownloadQRCodeUseCase**: Primary business logic interface for orchestrating QR code download operations

## Error Handling

The domain layer defines comprehensive error handling through sealed classes:

### DownloadError
- `PermissionDenied`: Storage permission was denied
- `InsufficientStorage`: Not enough storage space available
- `FileSystemError`: General file system error
- `UnknownError`: Unexpected error with descriptive message

### StorageError
- `DirectoryCreationFailed`: Failed to create directory structure
- `FileWriteFailed`: Failed to write file to storage
- `InsufficientSpace`: Not enough storage space
- `SystemError`: System-level error with exception details

## Integration

These interfaces are designed to integrate with the existing MVVM architecture:
- Use cases are injected into ViewModels via dependency injection
- Services abstract platform-specific implementations
- Models provide type-safe data transfer between layers
- Error handling provides comprehensive feedback for UI layer

## Requirements Mapping

This implementation addresses the following requirements:
- **4.1**: MVVM architecture integration with proper separation of concerns
- **4.5**: Consistent code patterns and naming conventions
- **1.1-1.5**: Core download functionality with proper error handling
- **2.1-2.5**: Permission management across API levels
- **5.1-5.5**: File management and storage operations