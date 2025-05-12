#!/bin/bash

find . \
  -type f \
  ! -path "*/venv/*" \
  ! -path "*/__pycache__/*" \
  ! -path "*/.git/*" \
  ! -path "*/.idea/*" \
  ! -path "*/.vscode/*" \
  ! -path "*/app/nn_model/*" \
  ! -name "*.pyc" \
  ! -name "*.png" \
  ! -name "*.jpg" \
  ! -name "*.jpeg" \
  ! -name "*.pdf" \
  ! -name "*.db" \
  ! -name "*.csv" \
  ! -name "*.sqlite" \
  ! -name "*.log" \
  ! -name "*.zip" \
  ! -name "*.tar" \
  ! -name "*.DS_Store" \
| sort | while read filepath; do
  echo ">>> $filepath" >> project_snapshot.txt
  echo "=====================================================================" >> project_snapshot.txt
  cat "$filepath" >> project_snapshot.txt
  echo -e "\n\n" >> project_snapshot.txt
done
