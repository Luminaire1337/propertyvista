#!/usr/bin/env bash
# Check if $1 is set to prod or dev and set the URL accordingly
url=""
if [ "$1" == "prod" ]; then
  echo "Starting Stripe listener in production mode..."
  url="http://api.localhost/payments/webhook"
else
  url="http://localhost:8080/payments/webhook"
fi

stripe listen \
  --events payment_intent.succeeded,payment_intent.payment_failed,payment_intent.canceled \
  --forward-to "$url"