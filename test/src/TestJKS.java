import java.security.PublicKey;
import java.util.Vector;

import sun.security.x509.X509CertImpl;

public class TestJKS {
	public static void main(String[] args) {
		try {
			GenericKey gKey = new GenericKey(
					false,
					"E:\\glassfish\\domains\\domain1\\config\\keystoreyuanlai.jks",
					"changeit".toCharArray(), null, 0, null);
			Vector vector = gKey.getM_TrustCerts();
			if ((vector != null) && (!vector.isEmpty())) {
				for (int j = 0; j < vector.size(); j++) {
					X509CertImpl x509CertImplLocal = (X509CertImpl) vector
							.get(j);
					PublicKey caPubKey = x509CertImplLocal.getPublicKey();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
