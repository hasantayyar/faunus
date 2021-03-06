package com.thinkaurelius.faunus;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.util.StringFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static com.tinkerpop.blueprints.Direction.IN;
import static com.tinkerpop.blueprints.Direction.OUT;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FaunusEdge extends FaunusElement implements Edge {

    private static final String DEFAULT = "_default";

    protected long outVertex;
    protected long inVertex;
    private String label;

    public FaunusEdge() {
        super(-1l);
        this.label = DEFAULT;
    }

    public FaunusEdge(final boolean enablePaths) {
        super(-1l);
        this.label = DEFAULT;
        this.enablePath(enablePaths);
    }

    public FaunusEdge(final DataInput in) throws IOException {
        super(-1l);
        this.readFields(in);
    }

    public FaunusEdge(final long outVertex, final long inVertex, final String label) {
        this(-1l, outVertex, inVertex, label);
    }

    public FaunusEdge(final long id, final long outVertex, final long inVertex, final String label) {
        super(id);
        this.outVertex = outVertex;
        this.inVertex = inVertex;
        setLabel(label);
    }

    public FaunusEdge reuse(final long id, final long outVertex, final long inVertex, final String label) {
        super.reuse(id);
        this.outVertex = outVertex;
        this.inVertex = inVertex;
        this.setLabel(label);
        return this;
    }

    public Vertex getVertex(final Direction direction) {
        if (OUT.equals(direction)) {
            return new FaunusVertex(this.outVertex);
        } else if (IN.equals(direction)) {
            return new FaunusVertex(this.inVertex);
        } else {
            throw ExceptionFactory.bothIsNotSupported();
        }
    }

    public long getVertexId(final Direction direction) {
        if (OUT.equals(direction)) {
            return this.outVertex;
        } else if (IN.equals(direction)) {
            return this.inVertex;
        } else {
            throw ExceptionFactory.bothIsNotSupported();
        }
    }

    public String getLabel() {
        return this.label;
    }

    protected final void setLabel(String label) {
        this.label = TYPE_MAP.get(label);
    }

    public void write(final DataOutput out) throws IOException {
        super.write(out);
        out.writeLong(this.inVertex);
        out.writeLong(this.outVertex);
        out.writeUTF(this.getLabel());
    }

    public void readFields(final DataInput in) throws IOException {
        super.readFields(in);
        this.inVertex = in.readLong();
        this.outVertex = in.readLong();
        setLabel(in.readUTF());
    }

    public void writeCompressed(final DataOutput out, final Direction idToWrite) throws IOException {
        super.write(out);
        if (idToWrite.equals(Direction.IN))
            out.writeLong(this.inVertex);
        else if (idToWrite.equals(Direction.OUT))
            out.writeLong(this.outVertex);
        else
            throw ExceptionFactory.bothIsNotSupported();
    }

    public void readFieldsCompressed(final DataInput in, final Direction idToRead) throws IOException {
        super.readFields(in);
        if (idToRead.equals(Direction.IN))
            this.inVertex = Long.valueOf(in.readLong());
        else if (idToRead.equals(Direction.OUT))
            this.outVertex = Long.valueOf(in.readLong());
        else
            throw ExceptionFactory.bothIsNotSupported();
        this.label = null;
    }

    public String toString() {
        return StringFactory.edgeString(this);
    }

    public int compareTo(final FaunusElement other) {
        return new Long(this.id).compareTo((Long) other.getId());
    }
}
