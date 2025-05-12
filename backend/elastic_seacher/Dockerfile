FROM python:3.7-slim

# Install system dependencies needed for building Python packages
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    gcc \
    libffi-dev \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Install the latest stable Rust + Cargo using rustup
RUN curl https://sh.rustup.rs -sSf | sh -s -- -y
# Add Cargo to PATH
ENV PATH="/root/.cargo/bin:${PATH}"

# (Optional) Create a virtual environment
RUN python3 -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

WORKDIR /app

# Copy requirements first so pip install can be cached
COPY requirements.txt /app/requirements.txt

# Upgrade pip and install all your dependencies
RUN pip install --upgrade pip
RUN pip install -r requirements.txt



# Copy remaining application files
COPY . /app

COPY app/nn_model/all-mpnet-base-v2 /opt/venv/bin/app/nn_model/all-mpnet-base-v2


# 5. (Optional) Force download the Sentence Transformers model
#    so it’s included in the image at build time.
#    Adjust the model name below if you use a different one.
ENV TRANSFORMERS_CACHE=/app/transformers_cache

EXPOSE 8000


CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
