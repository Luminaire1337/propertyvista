# Property Vista

## Prerequisites

Before running the application, ensure you have the following installed:

- **Unix-like host system** (Linux, macOS, or WSL) - required for bash scripts
- **Docker** with **Docker Compose**
- **Stripe CLI** - installed and logged into your Stripe account

## Getting Started

### Demo Mode

To start the application in demo mode:

```bash
./start_demo.sh
```

This will start both Docker Compose and Stripe webhook listener.

Once running, the application will be available at:

- **Website**: http://localhost
- **API**: http://api.localhost
- **Storage**: http://storage.localhost
- **Mailpit** (email testing): http://localhost:8025
- **Traefik Console**: http://localhost:8080
- **Storage Management Panel**: http://localhost:9001

## Environment Variables

The following variables are defined in `.env.local`. You can override any of these in a `.env` file, as Docker Compose loads `.env.local` first and then `.env`:

| Variable                 | Description                               | Required | Notes                                               |
| ------------------------ | ----------------------------------------- | -------- | --------------------------------------------------- |
| `DB_NAME`                | Database name                             | Yes      | Default: `propertyvista`                            |
| `DB_USER`                | Database username                         | Yes      | Default: `propertyvista`                            |
| `DB_PASSWORD`            | Database password                         | Yes      | Default: `propertyvista`                            |
| `MAIL_HOST`              | Mail server host                          | Yes      | Default: `mailpit` for demo                         |
| `MAIL_PORT`              | Mail server port                          | Yes      | Default: `1025`                                     |
| `MAIL_USERNAME`          | Mail server username                      | Yes      | Default: `mailpit`                                  |
| `MAIL_PASSWORD`          | Mail server password                      | Yes      | Default: `mailpit`                                  |
| `MAIL_FROM`              | Email sender address                      | Yes      | Default: `noreply@propertyvista.pl`                 |
| `JWT_SECRET`             | Secret key for JWT tokens                 | Yes      | Change default value in production                  |
| `JWT_EXPIRATION_MS`      | JWT token expiration time in milliseconds | Yes      | Default: `86400000` (1 day)                         |
| `STORAGE_ACCESS_KEY`     | S3 storage access key                     | Yes      | Default: `storageaccesskey`                         |
| `STORAGE_SECRET_KEY`     | S3 storage secret key                     | Yes      | Default: `storagesecretkey`                         |
| `GOOGLE_CLOUD_KEY`       | Google Cloud Vision API key               | No       | Optional - image verification won't work without it |
| `STRIPE_PUBLISHABLE_KEY` | Stripe publishable key                    | Yes      | You must provide your own Stripe keys               |
| `STRIPE_SECRET_KEY`      | Stripe secret key                         | Yes      | You must provide your own Stripe keys               |
| `STRIPE_WEBHOOK_KEY`     | Stripe webhook signing key                | Yes      | You must provide your own Stripe keys               |
| `FIRST_USER_EMAIL`       | Initial admin user email                  | Yes      | Default: `admin@admin.com`                          |
| `FIRST_USER_PASSWORD`    | Initial admin user password               | Yes      | Default: `Admin123#`                                |

## License

This project is licensed under the GNU General Public License v3.0. See the [LICENSE](LICENSE) file for details.
