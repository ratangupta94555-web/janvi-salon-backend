# Janvi Salon Backend

## Gallery uploads

Gallery uploads use signed Cloudinary uploads from the backend. The cloud name
defaults to the one shown in the shared Cloudinary screenshot; configure or
override it, along with these environment variables, before starting Spring Boot:

- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`
- `GALLERY_ADMIN_TOKEN` - a separate, private token used by the admin Gallery
  page; do not reuse the Cloudinary API secret.

The Cloudinary API secret and gallery admin token must remain server-side. The
admin Gallery page asks for the gallery token for each browser session and sends
it in the `X-Gallery-Admin-Token` header.

`GET /api/gallery` returns published media. `POST /api/gallery/admin/upload`
accepts multipart `images` (up to 5), an optional single `video`, and a
`category` (`Makeup`, `Hair`, `Bridal`, or `Studio`). Images are limited to
10 MB each and video to 100 MB. Uploaded files are stored in Cloudinary and
their public URLs and metadata are stored in the `gallery_media` database table.

For local development in PowerShell, set the environment variables in the
terminal used to start the backend:

```powershell
$env:CLOUDINARY_CLOUD_NAME = "your-cloud-name"
$env:CLOUDINARY_API_KEY = "your-api-key"
$env:CLOUDINARY_API_SECRET = "your-api-secret"
$env:GALLERY_ADMIN_TOKEN = "a-private-admin-token"
```
