package de.petendi.ethereum.signer;

/*-
 * #%L
 * ethereum-offline-signer
 * %%
 * Copyright (C) 2016 P-ACS UG (haftungsbeschränkt)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChain;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.kohsuke.args4j.CmdLineException;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.SignatureException;


public class Application {

    private static CmdLineResult cmdLineResult;

    public static void main(String[] args) throws UnreadableWalletException, SignatureException {

        Logger.getRootLogger().setLevel(Level.FATAL);

        try {
            cmdLineResult = new CmdLineResult();
            cmdLineResult.parseArguments(args);
        } catch (CmdLineException e) {
            System.out.println(e.getMessage());
            System.out.println("Usage:");
            cmdLineResult.printExample();
            System.exit(-1);
            return;
        }

        if (cmdLineResult.isCreate()) {
            create();
        } else if (cmdLineResult.isDerive()) {
            derive(cmdLineResult.getSeed(), cmdLineResult.getIteration());
        } else if (cmdLineResult.isSign()) {
            sign(cmdLineResult.getKey(), cmdLineResult.getTransaction());
        } else if(cmdLineResult.getTransactionDetails() != null) {
            byte[] transactionBytes = Hex.decode(cmdLineResult.getTransactionDetails().replace("0x", ""));
            Transaction transactionObj = new Transaction(transactionBytes);
            printTransactionDetails(transactionObj);
        } else {
            System.out.println("Usage:");
            cmdLineResult.printExample();
            System.exit(-1);
        }

    }

    private static void sign(String privateKey, String transaction) throws SignatureException {
        byte[] privBytes = Hex.decode(privateKey);
        byte[] transactionBytes = Hex.decode(transaction.replace("0x", ""));
        Transaction transactionObj = new Transaction(transactionBytes);
        transactionObj.sign(ECKey.fromPrivate(privBytes));
        System.out.println("Signed transaction:");
        System.out.println("0x" + Hex.toHexString(transactionObj.getEncodedRaw()));
        System.out.println("");
        printTransactionDetails(transactionObj);
    }

    private static void derive(String seed, int iteration) throws UnreadableWalletException {
        Wallet wallet = Wallet.fromSeed(NetworkParameters.fromID(NetworkParameters.ID_MAINNET),
                new DeterministicSeed(seed, null, "", 0));
        DeterministicKey key = null;
        for (int i = 0; i <= iteration; i++) {
            key = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        }
        ECKey ecKey = ECKey.fromPrivate(key.getPrivKey());
        System.out.println("Address: ");
        System.out.println("0x" + Hex.toHexString(ecKey.getAddress()));
        System.out.println("Key:");
        System.out.println(Hex.toHexString(ecKey.getPrivKeyBytes()));
    }

    private static void create() {
        Wallet wallet = new Wallet(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));
        DeterministicSeed seed = wallet.getKeyChainSeed();
        System.out.println("Mnemonic:");
        for (String mnemonc : seed.getMnemonicCode()) {
            System.out.print(mnemonc);
            System.out.print(" ");
        }
        System.out.println("");
        byte[] seedBytes = seed.getSeedBytes();
        System.out.println("Seed bytes:");
        System.out.println(Hex.toHexString(seedBytes));
        ECKey fromBit = ECKey.fromPrivate(seedBytes);
        System.out.println("Root address:");
        System.out.println("0x" + Hex.toHexString(fromBit.getAddress()));
    }

    private static final void printTransactionDetails(Transaction transaction) throws SignatureException {
        System.out.println("Details:");
        if(transaction.isContractCreation()) {
            System.out.println("Contract creation");
        } else {
            System.out.println("To: " + "0x"+ Hex.toHexString(transaction.getReceiveAddress()));
        }
        BigInteger nonce = fromHexString(ByteUtil.toHexString(transaction.getNonce()));
        System.out.println("Nonce: " + nonce);
        BigInteger value = fromHexString(ByteUtil.toHexString(transaction.getValue()));
        System.out.println("Value: " + value);
        BigInteger gasPrice = fromHexString(ByteUtil.toHexString(transaction.getGasPrice()));
        System.out.println("Gas price: " + gasPrice);
        BigInteger gasLimit = fromHexString(ByteUtil.toHexString(transaction.getGasLimit()));
        System.out.println("Gas limit: " + gasLimit);
        if(transaction.getSignature() != null) {
            ECKey ecKey = ECKey.signatureToKey(transaction.getRawHash(),transaction.getSignature().toBase64());
            String signer = "0x"+ org.spongycastle.util.encoders.Hex.toHexString(ecKey.getAddress());
            System.out.println("Signed by: " + signer);
        } else {
            System.out.println("Unsigned");
        }

    }

    private static final BigInteger fromHexString(String hexString) {
        return new BigInteger(hexString.replace("0x",""), 16);
    }


}
