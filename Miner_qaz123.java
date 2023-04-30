import java.util.Arrays;

public class Miner_qaz123 implements Miner {
    BlockChain[] blocks;
    int roundsLeft;
    int miningIndex;

    // Choose the longest chain, tie-breaking in favor of the chain with the most
    // stake.
    private BlockChain getDesiredChain() {
        BlockChain desiredChain = blocks[0];
        int maxRank = 0;
        double maxStake = 0;

        for (BlockChain block : blocks) {
            boolean shouldUpdate = (block.getRank() > maxRank)
                    || (block.getRank() == maxRank && block.getStakeForMiner(miningIndex) > maxStake);

            if (shouldUpdate) {
                maxRank = block.getRank();
                maxStake = block.getStakeForMiner(miningIndex);
                desiredChain = block;
            }
        }

        return desiredChain;
    }

    private double riskAssessment(double spendRatio) {
        // Perform risk assessment based on remaining rounds and miner's stake in the
        // desired chain.
        BlockChain desiredChain = getDesiredChain();
        double stake = desiredChain.getStakeForMiner(miningIndex);
        double adjustedSpendRatio = spendRatio;

        if (roundsLeft <= 3) {
            adjustedSpendRatio = Math.min(0.75, spendRatio * (1 + stake / 4));
        } else if (roundsLeft > 3 && roundsLeft <= 5) {
            adjustedSpendRatio = Math.min(0.5, spendRatio * (1 + stake / 8));
        }

        return adjustedSpendRatio;
    }

    public void refreshNetwork(BlockChain[] spendableBlocks, int myIndex, int roundsRemaining) {
        this.blocks = Arrays.copyOf(spendableBlocks, spendableBlocks.length);
        this.miningIndex = myIndex;
        this.roundsLeft = roundsRemaining;
    }

    public BlockChain getBlockToMine() {
        return getDesiredChain();
    }

    public double getAmountToSpend() {
        BlockChain desiredChain = getDesiredChain();
        double stake = desiredChain.getStakeForMiner(miningIndex);
        double spendRatio = 0.25;
        double adjustedSpendRatio = riskAssessment(spendRatio);

        return stake * adjustedSpendRatio;
    }

    public double getAmountToBribe() {
        // Adapt bribe amount based on the number of rounds left until mining
        double baseBribe = 1.0;
        if (roundsLeft > 0) {
            return baseBribe * (1.0 / roundsLeft);
        } else {
            return baseBribe;
        }
    }
}
