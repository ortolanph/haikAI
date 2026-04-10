package pt.pauloortolan.haikai.pojo;

import java.io.Serializable;

public record HaikaiRequest(String genre, String theme, String language) implements Serializable {
}
