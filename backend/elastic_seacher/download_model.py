import sys

from sentence_transformers import SentenceTransformer

model_path = f"{sys.path[1]}/app/nn_model/all-mpnet-base-v2"
print(model_path)
model = SentenceTransformer(model_path)

encoded = model.encode("Test sentence to download model")

print(encoded.tolist())
