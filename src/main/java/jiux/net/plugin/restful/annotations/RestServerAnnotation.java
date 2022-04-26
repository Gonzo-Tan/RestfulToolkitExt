package jiux.net.plugin.restful.annotations;

/**
 * @author QiLing
 * @date 2022/4/25 7:56 PM
 **/
public enum RestServerAnnotation implements PathMappingAnnotation {

    REST_SERVICE("RestService", "com.timevale.mandarin.common.annotation.RestService"),
    EXTERNAL_SERVICE("ExternalService", "com.timevale.mandarin.common.annotation.ExternalService"),;

    private final String shortName;

    private final String qualifiedName;

    RestServerAnnotation(String shortName, String qualifiedName) {
        this.shortName = shortName;
        this.qualifiedName = qualifiedName;
    }

    @Override
    public String getQualifiedName() {
        return this.qualifiedName;
    }

    @Override
    public String getShortName() {
        return this.shortName;
    }
}
