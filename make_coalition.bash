if [ -f "Miner_c1.java" ]; then
  rm Miner_c*
fi
for i in $(seq 1 10);
do
  cp Miner_coalitionqaz123.java "Miner_c$i.java"
  sed -i '' "1s/Miner_coalitionqaz123/Miner_c$i/" "Miner_c$i.java"
done
