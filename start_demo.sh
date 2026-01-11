#!/usr/bin/env bash
# Get all .env* files
ENV_FILES=""
for file in .env.local .env; do
    # Check if the file exists
    if [ ! -f "$file" ]; then
        continue
    fi

    # Check if this is a first entry
    if [ -z "$ENV_FILES" ]; then
        ENV_FILES="--env-file $file"
    else
        ENV_FILES="$ENV_FILES --env-file $file"
    fi
done

# https://stackoverflow.com/questions/3004811/how-do-you-run-multiple-programs-in-parallel-from-a-bash-script#comment74281615_5553774
docker compose \
    $ENV_FILES \
    -f docker-compose.base.yml \
    -f docker-compose.mailpit.yml \
    -f docker-compose.prod.yml \
    -p propertyvista \
    up --build \
    & ./start_stripe.sh prod && kill $!
