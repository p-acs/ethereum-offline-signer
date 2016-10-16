package de.petendi.ethereum.signer;

/*-
 * #%L
 * Ethereum Secure Proxy
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
import org.bitcoinj.wallet.*;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.kohsuke.args4j.CmdLineException;
import org.spongycastle.util.encoders.Hex;

import java.security.SignatureException;


public class Application {

    private static CmdLineResult cmdLineResult;

    public static void main(String[] args) throws UnreadableWalletException, SignatureException {

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
        Logger.getRootLogger().setLevel(Level.FATAL);



        if(cmdLineResult.isCreate()) {
            create();
            return;
        } else if(cmdLineResult.getDeriveKey() !=null) {
            derive(cmdLineResult.getDeriveKey(),cmdLineResult.getDerivationIteration());
        } else if(cmdLineResult.getPrivateKey() !=null) {
            sign(cmdLineResult.getPrivateKey(),cmdLineResult.getTransaction());
        } else {
            System.out.println("Usage:");
            cmdLineResult.printExample();
            System.exit(-1);
        }

    }

    private static void sign(String privateKey, String transaction) throws SignatureException {
        byte[] privBytes = Hex.decode(privateKey);
        byte[] transactionBytes = Hex.decode(transaction.replace("0x",""));
        Transaction transactionObj = new Transaction(transactionBytes);
        transactionObj.sign(ECKey.fromPrivate(privBytes));

        System.out.println("Signed transaction:");
        System.out.println("0x"+Hex.toHexString(transactionObj.getEncodedRaw()));

        ECKey ecKey = ECKey.signatureToKey(transactionObj.getRawHash(),transactionObj.getSignature().toBase64());
        String address = "0x"+ org.spongycastle.util.encoders.Hex.toHexString(ecKey.getAddress());
        System.out.println("Signed by");
        System.out.println(address);

    }

    private static void derive(String deriveKey, int derivationIteration) throws UnreadableWalletException {
        Wallet wallet = Wallet.fromSeed(NetworkParameters.fromID(NetworkParameters.ID_MAINNET),
                new DeterministicSeed(deriveKey,null,"",0));
        DeterministicKey key = null;
        for (int i = 0; i <= derivationIteration; i++) {
            key = wallet.freshKey(KeyChain.KeyPurpose.AUTHENTICATION);
        }
        System.out.println("Iteration path " + key.getPathAsString());
        ECKey ecKey = ECKey.fromPrivate(key.getPrivKey());
        System.out.println("Private key:");
        System.out.println(Hex.toHexString(ecKey.getPrivKeyBytes()));
        System.out.println("Address: ");
        System.out.println("0x"+Hex.toHexString(ecKey.getAddress()));


    }

    private static void create() {
        Wallet wallet = new Wallet(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));
        DeterministicSeed seed = wallet.getKeyChainSeed();

        System.out.println("Mnemonic:");

        for(String mnemonc: seed.getMnemonicCode()) {
            System.out.print(mnemonc);
            System.out.print(" ");
        }
        System.out.println("");
        byte [] seedBytes = seed.getSeedBytes();
        System.out.println("Seed bytes:");
        System.out.println(Hex.toHexString(seedBytes));
        ECKey fromBit = ECKey.fromPrivate(seedBytes);
        System.out.println("Root address:");
        System.out.println("0x"+Hex.toHexString(fromBit.getAddress()));

        for (int i = 0; i < 10; i++) {
            System.out.println("Iteration " + i);

            DeterministicKey deterministicKey = wallet.freshKey(KeyChain.KeyPurpose.AUTHENTICATION);
            System.out.println("Iteration path " + deterministicKey.getPathAsString());
            ECKey ecKey = ECKey.fromPrivate(deterministicKey.getPrivKey());
            System.out.println("Private key:");
            System.out.println(Hex.toHexString(ecKey.getPrivKeyBytes()));
            System.out.println("Address: ");
            System.out.println("0x"+Hex.toHexString(ecKey.getAddress()));
        }
    }


}
