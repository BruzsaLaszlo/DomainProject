package bruzsal.acmeclient;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.shredzone.acme4j.util.CertificateUtils;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.springframework.stereotype.Component;

import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Component
public class KeyGenerator {

    public KeyPair generateKeyPair(final String fileName, final int keysize) {
        try {
            KeyPair keyPair = createKeyPair(keysize);
            writeKeyPair(fileName, keyPair);
            return keyPair;
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new IllegalStateException("Can not create key files", e);
        }

    }

    public String generateCSR(final KeyPair keyPair, final String fileName,
                              final String distinguishedNames, final String... altNames) {
        try {
            PKCS10CertificationRequest csr = createCSR(keyPair, distinguishedNames, altNames);
            writeCSR(fileName, csr);
            return readFile(fileName);
        } catch (IOException | OperatorCreationException e) {
            throw new IllegalStateException(e);
        }
    }

    public KeyPair readKeyPair(final String fileName) throws IOException {
        if (new File(fileName).exists()) {
            try (FileReader reader = new FileReader(fileName)) {
                return KeyPairUtils.readKeyPair(reader);
            }
        }

        return null;
    }

    public void writeKeyPair(final String fileName, final KeyPair keyPair) throws IOException {
        try (JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(new FileWriter(fileName));) {
            jcaPEMWriter.writeObject(keyPair.getPrivate());
        }

        try (JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(new FileWriter(fileName + ".pub"));) {
            jcaPEMWriter.writeObject(keyPair.getPublic());
        }
    }

    public KeyPair createKeyPair(int keysize)
            throws NoSuchAlgorithmException {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keysize);

        return generator.generateKeyPair();
    }

    public PKCS10CertificationRequest readCSR(final String fileName) throws IOException {

        if (new File(fileName).exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
                return CertificateUtils.readCSR(fileInputStream);
            }
        }

        return null;
    }

    public void writeCSR(final String fileName, final PKCS10CertificationRequest csr) throws IOException {
        try (JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(new FileWriter(fileName));) {
            jcaPEMWriter.writeObject(csr);
        }
    }

    public PKCS10CertificationRequest createCSR(final KeyPair keyPair, final String distinguishedNames,
                                                final String... altNames)
            throws IOException, OperatorCreationException {

        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                new X500Principal(distinguishedNames), keyPair.getPublic());

        if (altNames != null && altNames.length > 0) {

            ASN1Encodable[] subjectAltNames = new ASN1Encodable[altNames.length];

            for (int index = 0; index < altNames.length; index++) {
                subjectAltNames[index] = new GeneralName(GeneralName.dNSName, altNames[index]);
            }

            DERSequence subjectAltNamesExtension = new DERSequence(subjectAltNames);

            ExtensionsGenerator extGen = new ExtensionsGenerator();
            extGen.addExtension(Extension.subjectAlternativeName, false, subjectAltNamesExtension);

            p10Builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGen.generate());
        }

        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        ContentSigner signer = csBuilder.build(keyPair.getPrivate());

        return p10Builder.build(signer);
    }

    public String readFile(String fileName) {
        try {
            return Files.readString(Path.of(fileName));
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not read file " + fileName, e);
        }
    }

}
