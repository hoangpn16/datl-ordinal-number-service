package fet.datn.factory;

import org.springframework.data.domain.Page;


public class PageResponseUtil {
    public static PageResponse buildPageResponse(Page page) {
        int currentPage = page.getNumber() + 1;
        if (currentPage <= page.getTotalPages()) {
            return new PageResponse(page.getTotalPages(), page.hasNext(), page.hasPrevious(), currentPage, page.getTotalElements());
        } else {
            return new PageResponse(page.getTotalPages(), null, null, null, page.getTotalElements());
        }
    }

    public static PageResponse buildPageResponse(Long totalElements, Integer pageNum, Integer pageSize) {

        Integer totalPage = (int) (long) totalElements / pageSize;
        if (totalPage * pageSize < totalElements) {
            totalPage += 1;
        }
        if (pageSize > totalElements) {
            totalPage = 1;
        }
        PageResponse pageResponse = null;
        if (pageNum < 0 || pageNum >= totalPage) {
            return null;
        }
        if (pageNum == 0) {
            if (totalPage == 1) {
                pageResponse = new PageResponse(totalPage, false, false, pageNum + 1, totalElements);
            } else {
                pageResponse = new PageResponse(totalPage, true, false, pageNum + 1, totalElements);
            }
        } else {
            if (pageNum == totalPage - 1) {
                pageResponse = new PageResponse(totalPage, false, true, pageNum + 1, totalElements);
            } else {
                pageResponse = new PageResponse(totalPage, true, true, pageNum + 1, totalElements);
            }
        }
        return pageResponse;
    }
}
