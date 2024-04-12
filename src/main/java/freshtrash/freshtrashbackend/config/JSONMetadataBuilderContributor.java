package freshtrash.freshtrashbackend.config;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class JSONMetadataBuilderContributor implements MetadataBuilderContributor {

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder.applySqlFunction(
                "JSON_CONTAINS", new StandardSQLFunction("JSON_CONTAINS", StandardBasicTypes.BOOLEAN));
        metadataBuilder.applySqlFunction(
                "JSON_QUOTE", new StandardSQLFunction("JSON_QUOTE", StandardBasicTypes.STRING));
    }
}
