package api;

import java.util.List;

public class ApiResponse<T> {
    private String get;
    private List<String> errors;
    private int results;
    private ApiPaging paging;
    private List<T> response;

    public String getGet() { return get; }
    public void setGet(String get) { this.get = get; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    public int getResults() { return results; }
    public void setResults(int results) { this.results = results; }
    public ApiPaging getPaging() { return paging; }
    public void setPaging(ApiPaging paging) { this.paging = paging; }
    public List<T> getResponse() { return response; }
    public void setResponse(List<T> response) { this.response = response; }
}