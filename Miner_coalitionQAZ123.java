import java.util.Arrays;

public class Miner_coalitionQAZ123 implements Miner {
    BlockChain[] blocks;
    int roundsLeft;
    int miningIndex;

    private static final double SIGNAL_SPEND = 0.000445;
    private static final double SIGNAL_BRIBE = 0.002023;

    private BlockChain getHighestRankChainWithBribe() {
        BlockChain desiredChain = blocks[0];
        Boolean foundCoalition = false;
        int maxRank = 0;
        // int earliest = Integer.MAX_VALUE;

        for (BlockChain block : blocks) {
            boolean shouldUpdate = (block.getBribeAmount() == SIGNAL_BRIBE && block.getRank() > maxRank);
            // || (block.getRank() == maxRank && block.getRoundCreated() < earliest);

            if (shouldUpdate) {
                // System.out.println("Found coalition");
                foundCoalition = true;
                maxRank = block.getRank();
                // earliest = block.getRoundCreated();
                desiredChain = block;
            }
        }
        
        if (foundCoalition){
            return desiredChain;
        }
        return blocks[0];

    }

    private boolean isSignalSent(BlockChain block) {
        if (block.getBribeAmount() == SIGNAL_BRIBE) {
            return true;
        }

        for (double spend : block.getSpends()) {
            if (spend == SIGNAL_SPEND) {
                return true;
            }
        }

        return false;
    }

    public void refreshNetwork(BlockChain[] spendableBlocks, int myIndex, int roundsRemaining) {
        this.blocks = Arrays.copyOf(spendableBlocks, spendableBlocks.length);
        this.roundsLeft = roundsRemaining;
        this.miningIndex = myIndex;
    }

    public BlockChain getBlockToMine() {
        return getHighestRankChainWithBribe();
    }

    public double getAmountToSpend() {
        BlockChain highestBlock = getHighestRankChainWithBribe();

        if (roundsLeft == 2 && !isSignalSent(highestBlock)) {
            return SIGNAL_SPEND;
        } else if (isSignalSent(highestBlock)) {
            return highestBlock.getStakeForMiner(miningIndex) * 0.35;
        } else {
            return 0.0;
        }
    }

    public double getAmountToBribe() {
        BlockChain highestBlock = getHighestRankChainWithBribe();

        if (isSignalSent(highestBlock)) {
            return 0.0;
        } else {
            return SIGNAL_BRIBE;
        }
    }
}
