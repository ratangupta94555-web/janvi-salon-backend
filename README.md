# Janvi Salon Backend

## Database setup

Configure the MySQL connection directly in
[application.properties](src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/janvisalondb?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=
```

Update these values to match your local or hosted MySQL instance.

## Gallery uploads

Gallery uploads use signed Cloudinary uploads from the backend. Configure these
environment variables before starting Spring Boot:

- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`
- `ADMIN_USERNAME`
- `ADMIN_PASSWORD`

The Cloudinary API key and secret must be a matching pair from the same
Cloudinary product environment. Keep the secret in deployment secrets or local
environment variables; do not commit it to `application.properties`.

Admin credentials are validated by the backend and establish an HTTP-only
session. The browser does not request or store a Cloudinary secret or gallery
upload token. Set `app.cors.allowed-origin` to the exact frontend origin when
the frontend and backend use different origins. For HTTPS deployments, set
`server.servlet.session.cookie.secure=true`; if the frontend is cross-site, set
`server.servlet.session.cookie.same-site=none`.

`GET /api/gallery` returns published media. `POST /api/gallery/admin/upload`
requires an authenticated admin session and accepts multipart `images` (up to
5), an optional single `video`, and a `category` (`Makeup`, `Hair`, `Bridal`, or
`Studio`). Images are limited to 10 MB each and video to 100 MB. Uploaded files
are stored in Cloudinary and their public URLs and metadata are stored in the
`gallery_media` database table.

Do not commit real passwords or API secrets in a shared source repository.
Rotate credentials that were previously stored in the application config, then
enter the new values in your private working copy of `application.properties`.
