using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PersonasService.Models
{
    public class BusinessLogicResponse
    {

        public int StatusCode { get; set; } = 200;

        public string Message { get; set; } = null!;

        public object ResponseObject { get; set; } = null!;

        public BusinessLogicResponse()
        {
        }

        public BusinessLogicResponse(int statusCode, string message)
        {
            StatusCode = statusCode;
            Message = message;
        }

    }
}
