package pt.pauloortolan.haikai.pojo;

import java.io.Serializable;

public record ImageHaikaiRequest(String line1, String line2, String line3) implements Serializable {
}
