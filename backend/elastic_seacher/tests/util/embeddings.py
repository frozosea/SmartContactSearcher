import json
import sys

import numpy as np
from sentence_transformers import SentenceTransformer
from sentence_transformers import util

# 1. Load a pretrained Sentence Transformer model
model = SentenceTransformer(f"{sys.path[1]}/app/nn_model/all-mpnet-base-v2")


# The sentences to encode

def load_data():
    with open("/Users/frozo/PycharmProjects/external-contact-searcher/tests/data/data_without_embs.json", "r") as file:
        data = json.load(file)
    return data


def save_data(data):
    # Convert any ndarray in data to a list
    for item in data:
        if isinstance(item["embedding"], np.ndarray):
            item["embedding"] = item["embedding"].tolist()  # Convert ndarray to list
    # Write the data to a JSON file
    with open("/Users/frozo/PycharmProjects/external-contact-searcher/tests/data/data_embs.json", "w") as file:
        file.write(json.dumps(data))  # Use json.dump() with indent for better readability


def _generate_embeddings(str_data):
    embeddings = model.encode(str_data)
    new_emvs = embeddings.tolist()
    print(len(new_emvs), len(embeddings))
    return new_emvs


def generate_embeddings():
    data = load_data()
    for item in data:
        combined_text = " ".join(
            [item["name"], item["jobTitle"], " ".join(item["tags"]), item["notesCombined"]])
        item["embedding"] = _generate_embeddings(combined_text)
    save_data(data)




if __name__ == '__main__':
    generate_embeddings()

# [3, 384]

# 3. Calculate the embedding similarities
#
# tensor([[1.0000, 0.6660, 0.1046],
#         [0.6660, 1.0000, 0.1411],
#         [0.1046, 0.1411, 1.0000]])
