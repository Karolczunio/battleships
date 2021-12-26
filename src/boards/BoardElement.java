package boards;

public record BoardElement(Part part, int id, boolean isVisible) {
    char character(){
        if (isVisible){
            return part.character();
        }
        else {
            return 'X';
        }
    }
    public BoardElement getWithChangedPart(Part part){
        return new BoardElement(part, this.id, this.isVisible);
    }
    public BoardElement getWithChangedId(int id){
        return new BoardElement(this.part, id, this.isVisible);
    }
    public BoardElement getWithChangedVisibility(boolean isVisible){
        return new BoardElement(this.part, this.id, isVisible);
    }
}
