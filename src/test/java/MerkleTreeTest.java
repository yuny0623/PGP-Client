
import org.junit.Assert;
import org.junit.Test;
import org.pgp.tree.merkletree.HashAlgorithm;
import org.pgp.tree.merkletree.MerkleTree;
import org.pgp.tree.merkletree.Node;

import java.util.ArrayList;

public class MerkleTreeTest {
    @Test
    public void merkle_tree_generate_test(){
        // given
        ArrayList<String> dataBlocks = new ArrayList<>();
        dataBlocks.add("Captain America");
        dataBlocks.add("Iron Man");
        dataBlocks.add("God of thunder");
        dataBlocks.add("Doctor strange");

        // when
        Node root = MerkleTree.generateTree(dataBlocks);
        MerkleTree.printLevelOrderTraversal(root);

        String level1_val1 = HashAlgorithm.generateHash("Captain America");
        String level1_val2 = HashAlgorithm.generateHash("Iron Man");
        String level1_val3 = HashAlgorithm.generateHash("God of thunder");
        String level1_val4 = HashAlgorithm.generateHash("Doctor strange");

        String level2_val1 = HashAlgorithm.generateHash(level1_val1 + level1_val2);
        String level2_val2 = HashAlgorithm.generateHash(level1_val3 + level1_val4);

        String level3_val1 = HashAlgorithm.generateHash(level2_val1 + level2_val2); // root

        // then
        Assert.assertNotNull(root);
        Assert.assertEquals(level3_val1, root.getHash());
    }
}
