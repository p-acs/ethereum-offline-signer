# Ethereum offline signer

This tool offers you the ability to offline sign Ethereum transactions.

## Usage

### Create the master seed

The base for every interaction with this tool is creating a master seed.

    java -jar ethereum-offline-signer.jar --create
    
Sample output:

    Mnemonic:
    area spice river shift wheel decline tribe panic blouse fresh receive coin
    Seed bytes:
    f2ad6f5db2f0de644b1a18d2b4c14215da14e9e0ed1d173679cf551538973f0ce6bf4b82e8125c775e458d16a28a2218f375fadad51ec1547fc54ce76ff5f877
    Root address:
    0x9c4b91cccef185888f7d330c31b26083ffa23026

Backup the mnemonic and guard it well!

### Derive a key

With the master seed you will be able to create as much keys as you want.

    java -jar ethereum-offline-signer.jar --derive --iteration #ITERATION_NUMBER# --seed "#SEED_MNEMONIC#"

Sample output with the above mnemonic and iteration 5:

    Address:
    0x1a86c013a6b2f29e18b4ad75df0c25813a936b69
    Key:
    ffb67396e27a762efc86aa8390fb37c8268c01d5f01415229fc3ea2c19203bbe

In order to restore the same key, just remember the iteration number and which mnemonic you used.
In addition to the commandline output the tool creates 2 png images, which contain QR codes of the address and the key.
You can share the address and print its QR code. You can also import the key in your wallet application. 
Doing so ends the offline signing capability of this specific key. 

Only import the key, if you are aware of the security implications!!


### Sign a transaction

With the private key you can sign hex encoded Ethereum transactions.

    java -jar ethereum-offline-signer.jar --sign --key #KEY# --transaction #TRANSACTION#


Sample output with the above key and transaction 0xef83100000850ba43b740082520894a2a9143a910e641f81715c87a0172c9554bfec44888ac7230489e8000080808080 :

    Signed transaction:
    0xf86f83100000850ba43b740082520894a2a9143a910e641f81715c87a0172c9554bfec44888ac7230489e80000801ca02d736b8da41512753a303776517ff259a0c6f9386b1fe2b69f9050e74b6046eba0567d15ae2c9ee28483b0f89b5a49d413888246c61d298bbb6b88cd78cab2dc4f
    
    Details:
    To: 0xa2a9143a910e641f81715c87a0172c9554bfec44
    Nonce: 1048576
    Value: 10000000000000000000
    Gas price: 50000000000
    Gas limit: 21000
    Signed by: 0x1a86c013a6b2f29e18b4ad75df0c25813a936b69


You can copy the signed transaction and paste it into your wallet application or you can use the png image with the QR code of this transaction,
 which was additionally created. In the above sample the file is named _transaction_0x50319245ce8838473e05f64caf3031c06a49da38577d85b22a2efca9590c00fe.png_.
 

### Show transaction details

You can inspect hex encoded Ethereum transactions.

    java -jar ethereum-offline-signer.jar --transactionDetails #TRANSACTION#
    
Sample output with the above unsigned transaction:

    Details:
    To: 0xa2a9143a910e641f81715c87a0172c9554bfec44
    Nonce: 1048576
    Value: 10000000000000000000
    Gas price: 50000000000
    Gas limit: 21000
    Unsigned



Contact us for questions: [info@p-acs.com](mailto:info@p-acs.com)



