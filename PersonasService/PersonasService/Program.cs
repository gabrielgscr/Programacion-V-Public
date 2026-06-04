using Microsoft.EntityFrameworkCore;
using PersonasService.DataAccess.Models;
using PersonasService;
using PersonaService.Abstract;
using PersonaService.BusinessLogic;
using System.Text.Json.Serialization;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowReactApp",
        policy => policy
            //.WithOrigins("http://localhost:5173")
            //.WithOrigins("")// O el origen de app que consuma el servicio
            .AllowAnyOrigin()
            .AllowAnyHeader()
            .AllowAnyMethod()
    );
});

// Add services to the container.
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddDbContext<Ejemplo2Context>(options =>
{
    options.UseSqlServer("name=ConnectionStrings:DefaultConnection");
});
builder.Services.AddScoped<IPersonaLogic, PersonaLogic>();
builder.Services.Configure<Microsoft.AspNetCore.Http.Json.JsonOptions>(options => options.SerializerOptions.ReferenceHandler = ReferenceHandler.IgnoreCycles);

var app = builder.Build();

// Configure the HTTP request pipeline.
//if (app.Environment.IsDevelopment() ) 
//{
    app.UseSwagger();
    app.UseSwaggerUI();
    app.UseCors("AllowReactApp");

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
};
//}

app.MapCustomEndpoints();
app.MapRolEndpoints();

app.MapPersonaEndpoints();

await app.RunAsync();
